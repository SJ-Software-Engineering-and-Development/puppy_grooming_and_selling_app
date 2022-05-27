package com.example.new_puppy.fragment;

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
import android.widget.Toast;

import com.example.new_puppy.R;
import com.example.new_puppy.activity.AppSharedPreferences;
import com.example.new_puppy.adapter.AdminPostRecyclerviewAdapter;
import com.example.new_puppy.adapter.UserBookedSlotRCAdapter;
import com.example.new_puppy.model.BookedSlot;
import com.example.new_puppy.model.BookedSlotCustom;
import com.example.new_puppy.model.BookingSlot;
import com.example.new_puppy.utils.ApiInterface;
import com.example.new_puppy.utils.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserBookingListFragment extends Fragment {

    static String apiBaseUrl = "";
    private static Context context;
    private static Activity activity;
    private static ProgressDialog progressDialog;
    private SharedPreferences sharedPre;

    private TextView noListingsItemsLabel, txtDate;
    private Button btnGoBack, btnGoForward;

    private static ArrayList<BookedSlot> bookedSlotsList = new ArrayList<BookedSlot>();

    private static ArrayList<BookedSlotCustom> bookedSlotCustomList = new ArrayList<BookedSlotCustom>();

    private static String dateSelected ="";
    private static String login_id ="";

    private static RecyclerView postsRecycler;
    private static UserBookedSlotRCAdapter userBookedSlotRCAdapter;

    Date dt;
    Calendar c;
    Format dateFormat;

    public UserBookingListFragment() {
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
        return inflater.inflate(R.layout.fragment_user_booking_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noListingsItemsLabel =(TextView) getView().findViewById(R.id.noListingsItemsLabel);
        txtDate =(TextView) getView().findViewById(R.id.txtDate);
        postsRecycler = (RecyclerView) getView().findViewById(R.id.userRecycler);

        txtDate.setText(dateFormat.format(dt));

        btnGoBack = ( Button) getView().findViewById(R.id.btnGoBack);
        btnGoForward = (Button) getView().findViewById(R.id.btnGoForward);

        noListingsItemsLabel.setText("Booking slots on " + dateSelected);

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait...");

        initOnClicks();
        getMyBookingSlots();
    }

    private void initOnClicks(){
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.add(Calendar.DATE, -1);
                dt = c.getTime();
                txtDate.setText(dateFormat.format(dt));
                dateSelected = dateFormat.format(dt);
                if(bookedSlotCustomList.size() > 0){
                    noListingsItemsLabel.setText("Booking slots on " + dateSelected);
                }else{
                    noListingsItemsLabel.setText("No Booking slots to show for " + dateSelected);
                }
                getMyBookingSlots();
            }
        });

        btnGoForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.add(Calendar.DATE, 1);
                dt = c.getTime();
                txtDate.setText(dateFormat.format(dt));
                dateSelected = dateFormat.format(dt);
                if(bookedSlotCustomList.size() > 0){
                    noListingsItemsLabel.setText("Booking slots on " + dateSelected);
                }else{
                    noListingsItemsLabel.setText("No Booking slots to show for " + dateSelected);
                }
                getMyBookingSlots();
            }
        });
    }


    private void getMyBookingSlots(){
        progressDialog.show();
        bookedSlotCustomList.clear();

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getBookedSlotsByUserId(dateSelected, login_id);

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
                                        bookedSlotCustomList
                                                .add(new BookedSlotCustom(
                                                        Integer.parseInt(bkSlotObj.getString("id")),
                                                        bkSlotObj.getString("package_type"),
                                                        bkSlotObj.getString("date"),
                                                        (bkSlotObj.getString("from_time")  +" - "+ bkSlotObj.getString("to_time")))
                                                );
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }
                        //Todo
                        if(bookedSlotCustomList.size() > 0){
                            noListingsItemsLabel.setText("Booking slots on " + dateSelected);
                        }
                    } else {
                        noListingsItemsLabel.setText("No Booking slots to show for " + dateSelected);
                        Toast.makeText(context, "No slots to show", Toast.LENGTH_LONG).show();
                        System.out.println("_==================Returned empty response");
                    }
                    setPostRecycler();
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

    private void  setPostRecycler(){
        // vaccineRecycler = getView().findViewById(R.id.vaccineRecycler); TODO : Move to onViewCreated()
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        postsRecycler.setLayoutManager(layoutManager);
        userBookedSlotRCAdapter = new UserBookedSlotRCAdapter(context, bookedSlotCustomList);
        postsRecycler.setAdapter(userBookedSlotRCAdapter);
    }
}