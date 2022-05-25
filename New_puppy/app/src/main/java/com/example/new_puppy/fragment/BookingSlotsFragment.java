package com.example.new_puppy.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.new_puppy.R;
import com.example.new_puppy.activity.AppSharedPreferences;
import com.example.new_puppy.adapter.BookSlotGVAdapter;
import com.example.new_puppy.model.BookedSlot;
import com.example.new_puppy.model.BookingSlot;
import com.example.new_puppy.model.Post;
import com.example.new_puppy.model.PostStatus;
import com.example.new_puppy.utils.ApiInterface;
import com.example.new_puppy.utils.RetrofitClient;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingSlotsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    static String apiBaseUrl = "";
    private static Context context;
    private static Activity activity;
    private static ProgressDialog progressDialog;
    private static Dialog appCustomDialog;
    private SharedPreferences sharedPre;

    private static GridView bookSLotGV;
    private TextView noListingsItemsLabel, txtDate;
    private Button btnGoBack, btnGoForward;
    Spinner booking_spinner;

    private static ArrayList<BookingSlot> bookingSlotsList = new ArrayList<BookingSlot>();
    private static ArrayList<BookedSlot> bookedSlotsList = new ArrayList<BookedSlot>();

    private static String dateSelected ="";
    private static String selectedPackage = "";
    private static String login_id ="";
    private static int selectedSlotId;

    String[] bookingTypes = {
            "Basic Groom(Rs.3500)",
            "Basic Groom VIP(Rs.4000)",
            "Full Groom(Rs.4900)",
            "Full Groom VIP(Rs.5400)",
            "Bath and Dry(Rs.2000)",
            "Bath and Dry VIP(Rs.2200)",
            "Ear Clean(Rs.800)",
            "Ear Clean VIP(Rs.1000)",
            "Groom(Rs.1000)",
            "Wool Cut(Rs.1600)",
            "Nails Clip(Rs.600)",
    };

    Date dt;
    Calendar c;
    Format dateFormat;

    //TODO: Ref: https://www.geeksforgeeks.org/gridview-in-android-with-example/

    public BookingSlotsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiBaseUrl =   apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        context = getContext();
        activity =getActivity();
        sharedPre = getActivity().getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);
        dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        this.login_id= AppSharedPreferences.getData(sharedPre,"login_id");

        dt=  new Date();;
        c= Calendar.getInstance();
        c.setTime(dt);
        dateSelected = dateFormat.format(dt);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booking_slots, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookSLotGV =(GridView) getView().findViewById(R.id.idGVcourses);
        noListingsItemsLabel =(TextView) getView().findViewById(R.id.noListingsItemsLabel);
        txtDate =(TextView) getView().findViewById(R.id.txtDate);

        txtDate.setText(dateFormat.format(dt));

        btnGoBack = ( Button) getView().findViewById(R.id.btnGoBack);
        btnGoForward = (Button) getView().findViewById(R.id.btnGoForward);

        noListingsItemsLabel.setText("Booking slots on " + dateSelected);

        initOnClicks();

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait...");

        getAllBookingSlots();

        initSubmitBookDialog();
        //        ArrayList<BookingSlot> bookingSlotArrayList = new ArrayList<BookingSlot>();
    //        bookingSlotArrayList.add(new BookingSlot(1, "", "", "", true));
    //
    //        BookSlotGVAdapter adapter = new BookSlotGVAdapter(context, bookingSlotArrayList);
    //        bookSLotGV.setAdapter(adapter);
    }

    public static void slotOnclick(int slotId, boolean isAvailable, String slotText){
        if(!isAvailable)return;

        selectedSlotId = slotId;
        showConfirmDialog(context, "Booking a slot", "Do you want to book?",
                ()->{
                    //TODO: book now
                    Chip chip_date = appCustomDialog.findViewById(R.id.chip_date);
                    Chip chip_slot = appCustomDialog.findViewById(R.id.chip_slot);
                    chip_date.setText(dateSelected);
                    chip_slot.setText(slotText);
                    appCustomDialog.show();
                }, ()->{
                    //TODO: not now
                });
    }

    private void initOnClicks(){
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.add(Calendar.DATE, -1);
                dt = c.getTime();
                txtDate.setText(dateFormat.format(dt));
                dateSelected = dateFormat.format(dt);
                if(bookingSlotsList.size() > 0){
                    noListingsItemsLabel.setText("Booking slots on " + dateSelected);
                }else{
                    noListingsItemsLabel.setText("No Booking slots to show for " + dateSelected);
                }
                getAllBookingSlots();
            }
        });

        btnGoForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.add(Calendar.DATE, 1);
                dt = c.getTime();
                txtDate.setText(dateFormat.format(dt));
                dateSelected = dateFormat.format(dt);
                if(bookingSlotsList.size() > 0){
                    noListingsItemsLabel.setText("Booking slots on " + dateSelected);
                }else{
                    noListingsItemsLabel.setText("No Booking slots to show for " + dateSelected);
                }
                getAllBookingSlots();
            }
        });
    }

    private void getAllBookingSlots(){
        progressDialog.show();
        bookingSlotsList.clear();

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getBookingSlots();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) { //Response code : 200
                    if (response.body() != null) {
                         System.out.println("_==================onSuccessimg");
                           System.out.println(response.body().toString());
                        try {
                            //  JSONArray jsonArray = new JSONArray(response.body().toString());
                            JSONObject jsnobject = new JSONObject(response.body().toString());
                            if(jsnobject.getString("success").equals("1")){
                                JSONArray jsonArray = new JSONArray(jsnobject.getString("data"));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    //  JSONObject explrObject = jsonArray.getJSONObject(i);
                                    JSONObject bkSlotObj =  new JSONObject(jsonArray.get(i).toString());
                                    bookingSlotsList.add(new BookingSlot(Integer.parseInt(bkSlotObj.getString("id")),bkSlotObj.getString("from_time"),bkSlotObj.getString("to_time"), bkSlotObj.getString("description"), true));
                                }
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }
                        //Todo display posts
                        if(bookingSlotsList.size() > 0){

                            noListingsItemsLabel.setText("Booking slots on " + dateSelected);
                        }else{
                            noListingsItemsLabel.setText("No Booking slots to show for " + dateSelected);
                            Toast.makeText(context, "No slots to show", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        System.out.println("_==================Returned empty response");
                    }
                    getBookedSlots();
                    progressDialog.dismiss();
                } else { //Response code : 400 response.code()
                    progressDialog.dismiss();
                    try {
                        String error = response.errorBody().string();
                        System.out.println("_==================Error: "+error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showDialog(context, "Opps..!", "Error", () -> {
                    });
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
    }


    private static void getBookedSlots(){
        progressDialog.show();
        bookedSlotsList.clear();

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getBookedSlots(dateSelected);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) { //Response code : 200
                    if (response.body() != null) {
                        System.out.println("_==================onSuccessimg");
                        System.out.println(response.body().toString());
                        try {
                            //  JSONArray jsonArray = new JSONArray(response.body().toString());
                            JSONObject jsnobject = new JSONObject(response.body().toString());
                            if(jsnobject.getString("success").equals("1")){
                                if(!jsnobject.getString("data").equals(""))
                                {
                                    JSONArray jsonArray = new JSONArray(jsnobject.getString("data"));
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        //  JSONObject explrObject = jsonArray.getJSONObject(i);
                                        JSONObject bkSlotObj =  new JSONObject(jsonArray.get(i).toString());
                                        bookedSlotsList
                                                .add(new BookedSlot(
                                                        Integer.parseInt(bkSlotObj.getString("id")),
                                                        bkSlotObj.getString("package_type"),
                                                        bkSlotObj.getString("date"),
                                                        Integer.parseInt(bkSlotObj.getString("user_id")),
                                                        Integer.parseInt(bkSlotObj.getString("booking_slot_id"))));
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }

                        //Todo
                        if(bookedSlotsList.size() > 0){
                        }
                    } else {
                        System.out.println("_==================Returned empty response");
                    }
                    fillSlotsInView();
                    progressDialog.dismiss();
                } else { //Response code : 400 response.code()
                    progressDialog.dismiss();
                    try {
                        String error = response.errorBody().string();
                        System.out.println("_==================Error: "+error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showDialog(context, "Opps..!", "Error", () -> {
                    });
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
    }

    private static void submitBookSlots(String package_type,String  date,  String user_id,  String booking_slot_id){
        progressDialog.show();

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.bookSlot(package_type, date, user_id, booking_slot_id);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) { //Response code : 200
                    if (response.body() != null) {
                        System.out.println("_==================onSuccessimg");
                        System.out.println(response.body().toString());
                        try {
                            //  JSONArray jsonArray = new JSONArray(response.body().toString());
                            JSONObject jsnobject = new JSONObject(response.body().toString());
                            if(jsnobject.getString("success").equals("1")){
                                showDialog(context, "Thank you!", "You have successfully make a book.", () -> {
                                    getBookedSlots();
                                });
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("_==================Returned empty response");
                    }
                    appCustomDialog.dismiss();
                    progressDialog.dismiss();
                } else { //Response code : 400 response.code()
                    progressDialog.dismiss();
                    try {
                        String error = response.errorBody().string();
                        System.out.println("_==================Error: "+error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showDialog(context, "Opps..!", "Error", () -> {
                    });
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
    }

    private static void fillSlotsInView(){

        for (BookingSlot bk: bookingSlotsList) {
            for(BookedSlot bked: bookedSlotsList){
                if(bk.getId() == bked.getBooking_slot_id()){
                    bk.setAvailable(false);
                }
            }
        }

        BookSlotGVAdapter adapter = new BookSlotGVAdapter(context, bookingSlotsList);
        bookSLotGV.setAdapter(adapter);
    }


    private void initSubmitBookDialog() {
        // TODO: Custom Dialog Start ::::::::::::::::::::::::::::::::::::::::::
        appCustomDialog = new Dialog(context);
        // >> TODO: Add Dialog Layout
        appCustomDialog.setContentView(R.layout.dialog_layout_submit_book);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            appCustomDialog.getWindow().setBackgroundDrawable(activity.getDrawable(R.drawable.alert_background));
        }
        appCustomDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        appCustomDialog.setCancelable(false);
        appCustomDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView labelFormHeading = appCustomDialog.findViewById(R.id.labelFormHeading);

        Chip chip_date = appCustomDialog.findViewById(R.id.chip_date);
        Chip chip_slot = appCustomDialog.findViewById(R.id.chip_slot);

        Spinner booking_spinner = appCustomDialog.findViewById(R.id.booking_spinner);
        booking_spinner.setOnItemSelectedListener(this);

        ArrayAdapter adapter = new ArrayAdapter(
                context,
                android.R.layout.simple_spinner_item,
                bookingTypes
        );
        //set layout res for each item of spiner
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        booking_spinner.setAdapter(adapter);

        Button btn_dialog_btnCancel = appCustomDialog.findViewById(R.id.btn_dialog_btnCancel);
        Button btn_dialog_btnAdd = appCustomDialog.findViewById(R.id.btn_dialog_btnAdd);
        // labelFormHeading.setText("Add New ...");

        btn_dialog_btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCustomDialog.dismiss();
            }
        });

        btn_dialog_btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(context, "Confirm", "Confirm?",
                        ()->{
                            //TODO: book now
                             submitBookSlots(selectedPackage, dateSelected, login_id, String.valueOf(selectedSlotId));
                        }, ()->{
                            //TODO: not now
                        });
            }
        });
        // TODO: Custom Dialog End :::::::::::::::::::::::::::::::::::::::::
    }

    public static  void showDialog(
            @NonNull final Context context,
            String title,
            String message,
            @Nullable Runnable confirmCallback
    ) {
        //TODO: Add TextInput Programatically...
        //  E.g. TextInputEditText myInput = new TextInputEditText(getContext());
        //  MaterialAlertDialogBuilder(context).addView(myInput)  <- Possible

        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Ok",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (confirmCallback != null) confirmCallback.run();
                        })
                .show();
    }

    public static  void showConfirmDialog(
            @NonNull final Context context,
            String title,
            String message,
            @Nullable Runnable confirmCallback,
            @Nullable Runnable cancelCallback
    ) {
        //TODO: Add TextInput Programatically...
        //  E.g. TextInputEditText myInput = new TextInputEditText(getContext());
        //  MaterialAlertDialogBuilder(context).addView(myInput)  <- Possible

        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Book this slot Now",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (confirmCallback != null) confirmCallback.run();
                        })
                .setNegativeButton("Not now",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (cancelCallback != null) cancelCallback.run();
                        })
                .show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedPackage = bookingTypes[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}