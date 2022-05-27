package com.example.new_puppy.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.new_puppy.R;
import com.example.new_puppy.adapter.VeterinaryRVAdapter;
import com.example.new_puppy.model.SearchModel;
import com.example.new_puppy.model.Veterinary;
import com.example.new_puppy.utils.ApiInterface;
import com.example.new_puppy.utils.RetrofitClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;

public class VeterinaryFragment extends Fragment {

    private static String apiBaseUrl = "";
    SharedPreferences sharedPre;
    private static Context context;
    private Activity activity;
    public static ActivityResultLauncher<Intent> activityResultLaunchLocation;
    private static FragmentManager fragmentManager;

    private static RecyclerView veterinaryRecycler;
    private static VeterinaryRVAdapter veterinaryRVAdapter;

    private Button btnAdd, btnGotoMapVeterianry, btnRefreshList;

    private ProgressDialog progressDialog;
    private static Dialog appCustomDialog;

    private TextView labelNoOfItems;

    private static List<Veterinary> veterinaryList = new ArrayList<Veterinary>();
    private String selectedCity = "";

    public VeterinaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        activity =getActivity();
        sharedPre= getActivity().getSharedPreferences("puppy_app",0);
        apiBaseUrl =   apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        progressDialog = new ProgressDialog(context);
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_veterinary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAdd = (Button) getView().findViewById(R.id.btnAdd);
        btnGotoMapVeterianry = (Button) getView().findViewById(R.id.btnGotoMapVeterianry);
        btnRefreshList = (Button) getView().findViewById(R.id.btnRefreshList);
        labelNoOfItems = (TextView) getView().findViewById(R.id.labelNoOfItems);

        veterinaryRecycler = (RecyclerView) getView().findViewById(R.id.userRecycler);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCustomDialog.show();
            }
        });

        btnGotoMapVeterianry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnRefreshList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVeterianries();
            }
        });

        initActivityResultLaunch();
        initCustomDialog();

        getVeterianries();
    }

    private void initActivityResultLaunch(){
        activityResultLaunchLocation = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Chip chip_location;
                        TextInputEditText txt_latitude, txt_longitude;
                        chip_location = appCustomDialog.findViewById(R.id.chip_location);
                        txt_longitude = appCustomDialog.findViewById(R.id.txt_longitude);
                        txt_latitude = appCustomDialog.findViewById(R.id.txt_latitude);

                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            Place place = PlacePicker.getPlace(data, context);
                            StringBuilder stringBuilder = new StringBuilder();
                            String latitude = String.valueOf(place.getLatLng().latitude);
                            String longitude = String.valueOf(place.getLatLng().longitude);
                            System.out.println("=============== LON LAT : " + latitude + "  " + longitude);
                            chip_location.setText("Change location");
                            txt_longitude.setText(longitude);
                            txt_latitude.setText(latitude);
                        }else{
                            if(txt_longitude.getText().equals("")){
                                chip_location.setText("Pick location");
                            }
                        }
                    }
                });
    }

    private void initCustomDialog(){

        // TODO: Custom Dialog Start ::::::::::::::::::::::::::::::::::::::::::
        appCustomDialog = new Dialog(getContext());
        // >> TODO: Add Dialog Layout
        appCustomDialog.setContentView(R.layout.dialog_layout_add_veterinary);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            appCustomDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.alert_background));
        }
        appCustomDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        appCustomDialog.setCancelable(false);
        appCustomDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextInputEditText txt_latitude, txt_longitude, txt_title, txt_address, txt_contact, txt_openAt, txt_closeAt;

        TextView labelFormHeading = appCustomDialog.findViewById(R.id.labelFormHeading);
        Chip chip_location = appCustomDialog.findViewById(R.id.chip_location);
        Chip chip_add_city = appCustomDialog.findViewById(R.id.chip_add_city);
        txt_longitude = appCustomDialog.findViewById(R.id.txt_longitude);
        txt_latitude = appCustomDialog.findViewById(R.id.txt_latitude);

        txt_title = appCustomDialog.findViewById(R.id.txt_title);
        txt_address = appCustomDialog.findViewById(R.id.txt_address);
        txt_contact = appCustomDialog.findViewById(R.id.txt_contact);
        txt_openAt = appCustomDialog.findViewById(R.id.txt_openAt);
        txt_closeAt = appCustomDialog.findViewById(R.id.txt_closeAt);

        Button btn_dialog_btnCancel = appCustomDialog.findViewById(R.id.btn_dialog_btnCancel);
        Button btn_dialog_btnAdd = appCustomDialog.findViewById(R.id.btn_dialog_btnAdd);
        //labelFormHeading.setText("Add New "+ userType);
        chip_location.setText("Pick location");
        chip_add_city.setText("Choose City");
        txt_longitude.setText("");
        txt_latitude.setText("");

        chip_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    activityResultLaunchLocation.launch(builder.build(activity));
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        chip_add_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleSearchDialogCompat(getActivity(), "Search...",
                        "Search  city name here...?", null, getCityList(),
                        new SearchResultListener<SearchModel>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat dialog,
                                                   SearchModel item, int position) {

                                // If filtering is enabled, [position] is the index of the item in the filtered result, not in the unfiltered source
                                //   Log.d("_location_", item.getTitle().toString() );
                                chip_add_city.setText(item.getTitle());
                                selectedCity = item.getTitle();
                                System.out.println("===============selectedCity " + selectedCity);
                                dialog.dismiss();
                            }
                        }).show();
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

                String title= txt_title.getText().toString().trim();
                String city= selectedCity;
                String address= txt_address.getText().toString().trim();
                String contact= txt_contact.getText().toString().trim();
                String open_close_times= txt_openAt.getText().toString().trim() + "-" +txt_closeAt.getText().toString().trim() ;
                String longitude= txt_longitude.getText().toString().trim();
                String latitude= txt_latitude.getText().toString().trim();

                if(  title.equals("") || city.equals("") || address.equals("") || contact.equals("") || longitude.equals("") || latitude.equals("") || open_close_times.equals("")){
                    showDialog(getActivity(),"Opps..!", "All fields are required.", ()->{});
                }else{
                    //TODO: Display Confirm Dialog
                    showConfirmDialog(getActivity(),() -> {
                        //TODO: confirmCall
                        txt_title.setText("");
                        txt_address.setText("");
                        txt_contact.setText("");
                        txt_openAt.setText("");
                        txt_closeAt.setText("");
                        txt_longitude.setText("");
                        txt_latitude.setText("");
                        selectedCity="";
                        appCustomDialog.dismiss();
                        addVeterinary(new Veterinary(0, title, city, address, contact, open_close_times, longitude, latitude));
                    }, ()->{
                        //TODO: cancelCall
                        txt_title.setText("");
                        txt_address.setText("");
                        txt_contact.setText("");
                        txt_openAt.setText("");
                        txt_closeAt.setText("");
                        txt_longitude.setText("");
                        txt_latitude.setText("");
                        selectedCity="";
                    });
                }
            }
        });
        // TODO: Custom Dialog End ::::::::::::::::::::::::::::::::::::::::::
    }

    private ArrayList<SearchModel> getCityList(){
        List<String> listCity = Arrays.asList(getResources().getStringArray(R.array.city_list));
        ArrayList<SearchModel> items = new ArrayList<>();
        for(String city: listCity){
            items.add(new SearchModel(city));
        }
        return items;
    }

    private void addVeterinary(Veterinary veterinary){
        progressDialog.setTitle("Saving...");
        progressDialog.show();

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.addVeterinary(veterinary.getTitle(),
                                                        veterinary.getCity(),
                                                        veterinary.getAddress(),
                                                        veterinary.getContact(),
                                                        veterinary.getOpen_close_times(),
                                                        veterinary.getLongitude(),
                                                        veterinary.getLatitude());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) { //Response code : 200
                    if (response.body() != null) {
                        Log.i("onSuccessimg", response.body().toString());
                        try {
                            JSONObject jsnobject = new JSONObject(response.body().toString());
                            if(jsnobject.getString("success").equals("1")){
                                if(!jsnobject.getString("data").equals(""))
                                {
                                    showDialog(context, "Done..!", "New Veterinary added!", () -> {
                                        getVeterianries();
                                    });
                                }
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================JSONException");
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println("_==================Returned empty response");
                    }
                    appCustomDialog.dismiss();
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

    private void getVeterianries(){
        veterinaryList.clear();
        progressDialog.show();

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getVeterinaries();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) { //Response code : 200
                    if (response.body() != null) {
                        //  System.out.println("_==================onSuccessimg");
                        //   System.out.println(response.body().toString());
                        try {
                            //  JSONArray jsonArray = new JSONArray(response.body().toString());
                            JSONObject jsnobject = new JSONObject(response.body().toString());
                            if(jsnobject.getString("success").equals("1")){
                                if(!jsnobject.getString("data").equals(""))
                                {
                                    JSONArray jsonArray = new JSONArray(jsnobject.getString("data"));
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        //  JSONObject explrObject = jsonArray.getJSONObject(i);
                                        Veterinary veterinary = new Gson().fromJson(jsonArray.get(i).toString(), Veterinary.class);
                                       // post.setImageUrl(apiBaseUrl+"uploadedFiles/"+post.getImageUrl());
                                        veterinaryList.add(veterinary);
                                    }
                                }else{
                                    veterinaryList.clear();
                                    Toast.makeText(context, "No posts to show", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }

                        labelNoOfItems.setText(veterinaryList.size()+" items.");
                    } else {
                        System.out.println("_==================Returned empty response");
                    }
                    //Todo display in list
                    setRecycler();
                    progressDialog.dismiss();
                } else { //Response code : 400 response.code()
                    try {
                        String error = response.errorBody().string();
                        System.out.println("_==================Error: "+error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }
                //  appProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
    }

    public static void listItemOnClick(int id){
        Fragment fragment = new ViewVaterinaryFragment(id);
        replaceFragment(fragment);
    }

    private void  setRecycler(){
        // vaccineRecycler = getView().findViewById(R.id.vaccineRecycler); TODO : Move to onViewCreated()
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        veterinaryRecycler.setLayoutManager(layoutManager);
        veterinaryRVAdapter = new VeterinaryRVAdapter(context, veterinaryList);
        veterinaryRecycler.setAdapter(veterinaryRVAdapter);
    }

    private static void replaceFragment(Fragment fragment){
        //TODO: Goto View ViewDisasterFragmentViewDisasterFragment
        //  FragmentManager fragmentManager =  AddUsersFragment.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        //  fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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