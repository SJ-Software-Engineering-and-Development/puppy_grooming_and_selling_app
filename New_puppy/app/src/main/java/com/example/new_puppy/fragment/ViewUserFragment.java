package com.example.new_puppy.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.new_puppy.R;
import com.example.new_puppy.activity.AppSharedPreferences;
import com.example.new_puppy.model.User;
import com.example.new_puppy.utils.ApiInterface;
import com.example.new_puppy.utils.Navigation;
import com.example.new_puppy.utils.RetrofitClient;
import com.example.new_puppy.utils.UserStorage;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewUserFragment extends Fragment {

    static String apiBaseUrl = "";
    private ProgressDialog progressDialog;
    private static Context context;

    private int user_id;

    TextView txtFullName, txtUserRole, txtCity, txtEmail, txtContact, txtNIC, txtCurrentStatus;
    Chip chip_active_acc, chip_suspend_acc;

     private static final String  ACTIVE = "active";
     private static final String  SUSPEND = "suspended";

    public ViewUserFragment() {
        // Required empty public constructor
    }

    public ViewUserFragment(int user_id) {
        // Required empty public constructor
        this.user_id = user_id;
        Navigation.currentScreen = "ViewUserFragment";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        apiBaseUrl =   apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        progressDialog = new ProgressDialog(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtFullName = (TextView) getView().findViewById(R.id.txtFullName);
        txtUserRole = (TextView) getView().findViewById(R.id.txtUserRole);
        txtCity = (TextView) getView().findViewById(R.id.txtCity);
        txtEmail = (TextView) getView().findViewById(R.id.txtEmail);
        txtContact = (TextView) getView().findViewById(R.id.txtContact);
        txtNIC = (TextView) getView().findViewById(R.id.txtNIC);
        txtCurrentStatus = (TextView) getView().findViewById(R.id.txtCurrentStatus);

        chip_active_acc = (Chip) getView().findViewById(R.id.chip_active_acc);
        chip_suspend_acc = (Chip) getView().findViewById(R.id.chip_suspend_acc);

        chip_active_acc.setChipBackgroundColorResource(R.color.colorLightGreen);
        chip_active_acc.setTextColor(Color.parseColor("#FFFFFF"));

        chip_suspend_acc.setChipBackgroundColorResource(R.color.lightRed);
        chip_suspend_acc.setTextColor(Color.parseColor("#FFFFFF"));

        //TODO: Colors for Unselected mode
       //  chip_voteEligible.setChipBackgroundColorResource(R.color.colorLightGray);
       //  chip_voteEligible.setTextColor(Color.parseColor("#000000"));

        chip_active_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(context, ()->{
                    updateAccountStatus(ACTIVE);
                },()->{});
            }
        });

        chip_suspend_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(context, ()->{
                    updateAccountStatus(SUSPEND);
                },()->{});
            }
        });

        getUser();
    }

    private void getUser(){

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getUserById(String.valueOf(this.user_id));

        progressDialog.setTitle("please wait...");
        progressDialog.show();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) { //Response code : 200
                    if (response.body() != null) {
                          System.out.println("_==================getUsergetUsergetUsergetUser");
                          System.out.println(response.body().toString());
                        try {
                            //  JSONArray jsonArray = new JSONArray(response.body().toString());
                            JSONObject jsnobject = new JSONObject(response.body().toString());
                            if(jsnobject.getString("success").equals("1")){
                                JSONObject userObj = new JSONObject(jsnobject.getString("data"));
                                User user = new User(Integer.parseInt(userObj.getString("id")),userObj.getString("Full Name"),userObj.getString("Nic Number"),userObj.getString("City"),userObj.getString("User Type"),userObj.getString("E-mail"),userObj.getString("Contact No"));
                                String account_status = userObj.getString("status");
                                txtCurrentStatus.setText("This account is currently "+account_status);
                                switch (account_status){
                                    case "active":
                                        chip_active_acc.setVisibility(View.GONE);
                                        chip_suspend_acc.setVisibility(View.VISIBLE);
                                        break;
                                    case "suspended":
                                        chip_active_acc.setVisibility(View.VISIBLE);
                                        chip_suspend_acc.setVisibility(View.GONE);
                                        break;
                                }

                                //Save user in static variable
                                UserStorage.user = user;
                                updateView();
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }
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
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
    }

    private void updateAccountStatus(String newStatus){

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.updateAccountStatus(String.valueOf(this.user_id), newStatus);

        progressDialog.setTitle("please wait...");
        progressDialog.show();

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
                              showDialog(context, "Done!", "This Account has been "+newStatus, ()->{});
                              getUser();
                            }
                        } catch (JSONException e) {
                            System.out.println("_==================error");
                            e.printStackTrace();
                        }
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
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
    }

    private void updateView(){
        txtFullName.setText(UserStorage.user.getFullName());
        txtUserRole.setText(UserStorage.user.getUserType());
        txtCity.setText(UserStorage.user.getCity());
        txtEmail.setText(UserStorage.user.getEmail());
        txtContact.setText(UserStorage.user.getContactNo());
        txtNIC.setText(UserStorage.user.getNIC());
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
}