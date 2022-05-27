package com.example.new_puppy.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.new_puppy.R;
import com.example.new_puppy.activity.AppSharedPreferences;
import com.example.new_puppy.activity.Login;
import com.example.new_puppy.model.User;
import com.example.new_puppy.utils.ApiInterface;
import com.example.new_puppy.utils.RetrofitClient;
import com.example.new_puppy.utils.UserStorage;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfileFragment extends Fragment {

    private SharedPreferences sharedPre;
    static String apiBaseUrl = "";
    private String login_id ="";
    private ProgressDialog progressDialog;
    private static Context context;

    private CircleImageView userImage;
    private TextView tvOwnerName, tvOwnerEmail,tvRole;
    private TextInputEditText txtCurrentPassword, txtNewPassword, txtConfirmNewPassword;
    private Button btnLogout, btnGotoSettings,btnGoBack, btnUpdatePassoword;
    private MaterialCardView cardViewProfile, cardViewSettings;
    private static Dialog appProgressDialog;


    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        sharedPre= getActivity().getSharedPreferences("puppy_app",0);
        apiBaseUrl =   apiBaseUrl = getResources().getString(R.string.apiBaseUrl);
        this.login_id= AppSharedPreferences.getData(sharedPre,"login_id");
        progressDialog = new ProgressDialog(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userImage = (CircleImageView) getView().findViewById(R.id.imageItem);
        tvOwnerName = (TextView) getView().findViewById(R.id.tvOwnerName);
        tvOwnerEmail = (TextView) getView().findViewById(R.id.tvOwnerEmail);
        tvRole = (TextView) getView().findViewById(R.id.tvRole);
        btnGotoSettings = (Button) getView().findViewById(R.id.btnGotoSettings);
        btnGoBack = (Button) getView().findViewById(R.id.btnGoBack);
        btnUpdatePassoword = (Button) getView().findViewById(R.id.btnUpdatePassoword);
        cardViewProfile = (MaterialCardView) getView().findViewById(R.id.cardViewProfile);
        cardViewSettings = (MaterialCardView) getView().findViewById(R.id.cardViewSettings);
        txtCurrentPassword = (TextInputEditText) getView().findViewById(R.id.txtCurrentPassword);
        txtNewPassword = (TextInputEditText) getView().findViewById(R.id.txtNewPassword);
        txtConfirmNewPassword = (TextInputEditText) getView().findViewById(R.id.txtConfirmNewPassword);
        btnLogout = (Button) getView().findViewById(R.id.btnLogout);

        if(UserStorage.user!=null){
            if(UserStorage.user.getId() != 0){
                updateView();
            }else
                getUser(this.login_id);
        }else getUser(this.login_id);


        initEvents();
    }

    private void getUser(String id){

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getUserById(id);

        progressDialog.setTitle("please wait...");
        progressDialog.show();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) { //Response code : 200
                    if (response.body() != null) {
                        //  System.out.println("_==================onSuccessimg");
                        //  System.out.println(response.body().toString());
                        try {
                            //  JSONArray jsonArray = new JSONArray(response.body().toString());
                            JSONObject jsnobject = new JSONObject(response.body().toString());
                            if(jsnobject.getString("success").equals("1")){
                                JSONObject userObj = new JSONObject(jsnobject.getString("data"));
                                User user = new User(Integer.parseInt(userObj.getString("id")),userObj.getString("Full Name"),userObj.getString("Nic Number"),userObj.getString("City"),userObj.getString("User Type"),userObj.getString("E-mail"),userObj.getString("Contact No"));

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

    private void updateView(){
        tvOwnerName.setText(UserStorage.user.getFullName());
        tvOwnerEmail.setText(UserStorage.user.getEmail());
        tvRole.setText(UserStorage.user.getUserType());
    }

    private void initEvents(){
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppSharedPreferences.getData(sharedPre,"login_id") != null){
                    // Remove user's data from SharedPreferences
                    AppSharedPreferences.removeData(sharedPre,"login_id");
                    AppSharedPreferences.removeData(sharedPre,"user_role");
                    AppSharedPreferences.removeData(sharedPre,"user_nic");

                    //Clear Static variable
                    UserStorage.user = null;
                }
                startActivity(new Intent(getActivity(), Login.class));
            }
        });
        btnGotoSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewProfile.setVisibility(View.GONE);
                btnGotoSettings.setVisibility(View.GONE);
                cardViewSettings.setVisibility(View.VISIBLE);
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewProfile.setVisibility(View.VISIBLE);
                btnGotoSettings.setVisibility(View.VISIBLE);
                cardViewSettings.setVisibility(View.GONE);
            }
        });

        btnUpdatePassoword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(context, ()->{
                    if(txtNewPassword.getText().toString().trim().equals(txtConfirmNewPassword.getText().toString().trim())){
                        updatePassword(txtCurrentPassword.getText().toString().trim(), txtConfirmNewPassword.getText().toString().trim());
                    }else{
                        showDialog(context, "Oops...!","Confirm password Not match",()->{});
                    }
                },()->{

                });
            }
        });
    }


    private void updatePassword(String oldPassword, String newPassword){

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