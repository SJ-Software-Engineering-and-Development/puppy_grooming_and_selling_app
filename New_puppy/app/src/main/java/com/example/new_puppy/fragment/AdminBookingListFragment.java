package com.example.new_puppy.fragment;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.new_puppy.R;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.new_puppy.R;
import com.example.new_puppy.activity.AppSharedPreferences;
import com.example.new_puppy.adapter.AdminBookSlotGVAdapter;
import com.example.new_puppy.adapter.AdminPostRecyclerviewAdapter;
import com.example.new_puppy.adapter.BookSlotGVAdapter;
import com.example.new_puppy.adapter.UserBookedSlotRCAdapter;
import com.example.new_puppy.model.BookedSlot;
import com.example.new_puppy.model.BookedSlotCustom;
import com.example.new_puppy.model.BookingSlot;
import com.example.new_puppy.model.Breed;
import com.example.new_puppy.model.Post;
import com.example.new_puppy.model.PostStatus;
import com.example.new_puppy.model.SearchBreedModel;
import com.example.new_puppy.model.SearchModel;
import com.example.new_puppy.utils.ApiInterface;
import com.example.new_puppy.utils.BreedStorage;
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
import java.util.Locale;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminBookingListFragment extends Fragment {

    static String apiBaseUrl = "";
    private static Context context;
    private static Activity activity;
    private static Dialog appCustomDialog;
    private static ProgressDialog progressDialog;
    private SharedPreferences sharedPre;

    private static TextView noListingsItemsLabel;
    private Chip chip_add_slot;
    private static GridView bookSLotGV;

    private static String login_id ="";

    private static ArrayList<BookingSlot> bookingSlotsList = new ArrayList<BookingSlot>();

    private int hour, minute;
    private String selectedStartTime ="";
    private String selectedEndTime ="";

    public AdminBookingListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        context = getContext();
        activity =getActivity();
        sharedPre = getActivity().getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);

        this.login_id= AppSharedPreferences.getData(sharedPre,"login_id");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_booking_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookSLotGV =(GridView) getView().findViewById(R.id.idGVcourses);
        noListingsItemsLabel =(TextView) getView().findViewById(R.id.noListingsItemsLabel);
        chip_add_slot = (Chip) getView().findViewById(R.id.chip_add_slot);

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait...");

        initSlotAddDialog();

        chip_add_slot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCustomDialog.show();
                Chip chip_start_time = appCustomDialog.findViewById(R.id.chip_start_time);
                Chip chip_end_time = appCustomDialog.findViewById(R.id.chip_end_time);
                chip_start_time.setText("Start at");
                chip_end_time.setText("End at");
                selectedStartTime ="";
                selectedEndTime ="";
            }
        });

        getAllBookingSlots();
    }

    private void initSlotAddDialog() {
        // TODO: Custom Dialog Start ::::::::::::::::::::::::::::::::::::::::::
        appCustomDialog = new Dialog(getContext());
        // >> TODO: Add Dialog Layout
        appCustomDialog.setContentView(R.layout.dialog_layout_add_slot);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            appCustomDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.alert_background));
        }
        appCustomDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        appCustomDialog.setCancelable(false);
        appCustomDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView labelFormHeading = appCustomDialog.findViewById(R.id.labelFormHeading);
        TextInputEditText txt_description = appCustomDialog.findViewById(R.id.txt_description);

        Chip chip_start_time = appCustomDialog.findViewById(R.id.chip_start_time);
        Chip chip_end_time = appCustomDialog.findViewById(R.id.chip_end_time);
        chip_start_time.setText("Start at");
        chip_end_time.setText("End at");
        selectedStartTime ="";
        selectedEndTime ="";

        Button btn_dialog_btnCancel = appCustomDialog.findViewById(R.id.btn_dialog_btnCancel);
        Button btn_dialog_btnAdd = appCustomDialog.findViewById(R.id.btn_dialog_btnAdd);

        // labelFormHeading.setText("Add New ...");

        chip_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popStartTimePicker();
            }
        });

        chip_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popEndTimePicker();
            }
        });

        btn_dialog_btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCustomDialog.dismiss();
            }
        });

        btn_dialog_btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( txt_description.getText().toString().equals("") || selectedStartTime.equals("") || selectedEndTime.equals("")
                ){
                    showDialog(getActivity(),"Opps..!", "All fields are required.", ()->{});
                }else{
                    //TODO: Display Confirm Dialog
                    showConfirmDialog(getActivity(),() -> {
                        //TODO: confirmCall
                        saveSlot(txt_description.getText().toString());
                        txt_description.setText("");
                    }, ()->{
                        //TODO: cancelCall
                        appCustomDialog.dismiss();
                        txt_description.setText("");
                    });
                }
            }
        });
        // TODO: Custom Dialog End :::::::::::::::::::::::::::::::::::::::::
    }

    private void saveSlot(String description){
        progressDialog.show();

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.addBookingSlot(selectedStartTime, selectedEndTime, description);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) { //Response code : 200
                    if (response.body() != null) {
                        //  System.out.println("_==================onSuccessimg");
                        System.out.println(response.body().toString());
                        try {
                            //  JSONArray jsonArray = new JSONArray(response.body().toString());
                            JSONObject jsnobject = new JSONObject(response.body().toString());
                            if(jsnobject.getString("success").equals("1")){
                               showDialog(context, "Done!", "New slot has been added", ()->{});
                               appCustomDialog.dismiss();
                                getAllBookingSlots();
                            }else{
                                showDialog(context, "Error!", "New slot cannot add", ()->{});
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }
                        //Todo display posts
                    } else {
                        System.out.println("_==================Returned empty response");
                    }
                    progressDialog.dismiss();
                } else { //Response code : 400 response.code()
                    try {
                        String error = response.errorBody().string();
                        System.out.println("_==================Error: "+error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showDialog(context, "Opps..!", "Error", () -> {
                    });
                    progressDialog.dismiss();
                }
                //  appProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                showDialog(context, "Opps..!", "Time out \nCould not connect", () -> {
                });
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
    }

    public void popStartTimePicker()
    {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {
                hour = selectedHour;
                minute = selectedMinute;
                //  timeButton.setText(String.format(Locale.getDefault(), "%02d:%02d",hour, minute));
                selectedStartTime = String.format(Locale.getDefault(), "%02d:%02d",hour, minute);
                Chip chip_start_time = appCustomDialog.findViewById(R.id.chip_start_time);
                chip_start_time.setText(selectedStartTime);
            }
        };
        // int style = AlertDialog.THEME_HOLO_DARK;
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, /*style,*/ onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Slot Start Time");
        timePickerDialog.show();
    }

    public void popEndTimePicker()
    {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {
                hour = selectedHour;
                minute = selectedMinute;
                //  timeButton.setText(String.format(Locale.getDefault(), "%02d:%02d",hour, minute));
                selectedEndTime = String.format(Locale.getDefault(), "%02d:%02d",hour, minute);
                Chip chip_end_time = appCustomDialog.findViewById(R.id.chip_end_time);
                chip_end_time.setText(selectedEndTime);
            }
        };
        // int style = AlertDialog.THEME_HOLO_DARK;
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, /*style,*/ onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Slot End Time");
        timePickerDialog.show();
    }

    private static void getAllBookingSlots(){
        progressDialog.show();
        bookingSlotsList.clear();
        fillSlotsInView();

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
                                fillSlotsInView();
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }
                        //Todo display posts
                        if(bookingSlotsList.size() == 0){
                            noListingsItemsLabel.setText("No Booking slots to show");
                        }
                    } else {
                        System.out.println("_==================Returned empty response");
                    }

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
        AdminBookSlotGVAdapter adapter = new AdminBookSlotGVAdapter(context, bookingSlotsList);
        bookSLotGV.setAdapter(adapter);
    }

    public static void slotOnclick(int slotId, boolean isAvailable, String slotText){
        showDeleteDialog(context, ()->{
            //delete
           showConfirmDialog(context, ()->{
               deleteYtbVideo(slotId);
           }, ()->{});
        }, ()->{});
    }


    private static void deleteYtbVideo(int id){
        progressDialog.show();

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.deleteSlot(String.valueOf(id));

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
                                showDialog(context, "Done!", "The Slot has been deleted", ()->{});
                                getAllBookingSlots();
                            }else{
                                showDialog(context, "Error!", "Couln't perform delete task", ()->{});
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================JSONException");
                            e.printStackTrace();
                        }

                    }
                    progressDialog.dismiss();
                } else { //Response code : 400 response.code()
                    progressDialog.dismiss();
                    try {
                        String error = response.errorBody().string();
                        System.out.println("_==================Error: "+error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
    }

    public static  void showConfirmDialog(
            @NonNull final Context context,
            @Nullable Runnable confirmCallback,
            @Nullable Runnable cancelCallback
    ) {
        //TODO: Add TextInput Programatically...
        //  E.g. TextInputEditText myInput = new TextInputEditText(getContext());
        //  MaterialAlertDialogBuilder(context).addView(myInput)  <- Possible

        new MaterialAlertDialogBuilder(context)
                .setTitle("Are you sure ?")
                .setMessage("Are you sure to continue this task")
                .setCancelable(false)
                .setPositiveButton("Confirm",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (confirmCallback != null) confirmCallback.run();
                        })
                .setNegativeButton("Cancel",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (cancelCallback != null) cancelCallback.run();
                        })
                .show();
    }

    public static  void showDeleteDialog(
            @NonNull final Context context,
            @Nullable Runnable confirmCallback,
            @Nullable Runnable cancelCallback
    ) {
        //TODO: Add TextInput Programatically...
        //  E.g. TextInputEditText myInput = new TextInputEditText(getContext());
        //  MaterialAlertDialogBuilder(context).addView(myInput)  <- Possible

        new MaterialAlertDialogBuilder(context)
                .setTitle("Actions")
                .setMessage("")
                .setCancelable(false)
                .setPositiveButton("Delete",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (confirmCallback != null) confirmCallback.run();
                        })
                .setNegativeButton("Cancel",
                        (dialog2, which) -> {
                            dialog2.dismiss();
                            if (cancelCallback != null) cancelCallback.run();
                        })
                .show();
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
}