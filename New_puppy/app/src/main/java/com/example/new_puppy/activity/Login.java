package com.example.new_puppy.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.AbsActionBarView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.new_puppy.R;
import com.example.new_puppy.model.User;
import com.example.new_puppy.utils.ApiInterface;
import com.example.new_puppy.utils.RetrofitClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private SharedPreferences sharedPre;

    private TextInputEditText textInputEditTextUsername,textInputEditTextPassword;
    private Button buttonLogin,btnNewuserSignUp;
    private Context context;
    private TextView LogoName;

    static String apiBaseUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context=this;

        apiBaseUrl =   apiBaseUrl = getResources().getString(R.string.apiBaseUrl);

        sharedPre= getSharedPreferences(getResources().getString(R.string.app_shared_pre), 0);

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait...");

        checkSignedIn();

        textInputEditTextUsername = findViewById(R.id.txtLoginUserName);
        textInputEditTextPassword = findViewById(R.id.txtLoginPassword);
        btnNewuserSignUp = findViewById(R.id.btnNewuserSignUp);
        buttonLogin = findViewById(R.id.btnUserLogin);
        LogoName = findViewById(R.id.LogoName);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String idno,password;

                idno = String.valueOf(textInputEditTextUsername.getText());
                password = String.valueOf(textInputEditTextPassword.getText());

                if (idno.equals("")
                        && !password.equals("")
                ){
                    Toast.makeText(getApplicationContext(),"All Fields are required!", Toast.LENGTH_SHORT).show();
                }
                else if (idno.equals("admin")
                        && password.equals("admin")
                ){
                    AppSharedPreferences.saveData(sharedPre,"user_nic", idno);
                    startActivity(new Intent(getApplicationContext(), AdminageContainerActivity.class));
                }
                else
                {
                //System.out.println("===========================================" );
                    //Start ProgressBar first (Set visibility VISIBLE)
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[2];
                            field[0] = "idno";
                            field[1] = "password";

                            //Creating array for data
                            String[] data = new String[2];
                            data[0] = idno;
                            data[1] = password;

                            PutData putData = new PutData(apiBaseUrl+"login.php", "POST", field, data);

                            if (putData.startPut()) {
                                if (putData.onComplete()) {

                                    String result = putData.getResult();
                                    //End ProgressBar (Set visibility to GONE)

                                    if(result.equals("Login Success")){
                                        getLoginId(idno);
                                    }
                                    else {
                                        Log.d("Login failed",result);
                                        Toast.makeText(getApplicationContext(),"Login failed"+result,Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                            //End Write and Read data with URL
                        }
                    });

                }
            }
        });

        btnNewuserSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,SignUp.class));
            }
        });

        LogoName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,Bath_House_Booking.class));
            }
        });
    }

    private void getLoginId(String nic){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[1];
                field[0] = "nic";

                //Creating array for data
                String[] data = new String[1];
                data[0] = nic;

                PutData putData = new PutData(apiBaseUrl+"getLoginId.php", "POST", field, data);

                if (putData.startPut()) {
                    if (putData.onComplete()) {

                        String result = putData.getResult();
                        //End ProgressBar (Set visibility to GONE)
                         System.out.println("======================= Result " + result);
                        if(!result.equals("0")){

                            AppSharedPreferences.saveData(sharedPre,"user_nic", nic);
                            AppSharedPreferences.saveData(sharedPre,"login_id", result);http://192.168.1.100/puppy_app_backend/
                            getUser(result);
                        }
                        else {
                            Log.d("Login failed",result);
                            Toast.makeText(getApplicationContext(),"Login failed"+result,Toast.LENGTH_SHORT).show();
                            System.out.println("======================= Result " + result);
                        }

                    }
                }
                //End Write and Read data with URL
            }
        });
    }

    private void redirectToHome(String userRole){
        switch (userRole){
            case "user":
                startActivity(new Intent(getApplicationContext(), UserPageContainerActivity.class));
                finish();
                break;
            case "admin":
                startActivity(new Intent(getApplicationContext(), AdminageContainerActivity.class));
                finish();
                break;
        }
    }

    private void checkSignedIn(){
        if(AppSharedPreferences.getData(sharedPre,"login_id")!=null){
            if(AppSharedPreferences.getData(sharedPre,"login_id")!=""){
                getUser(AppSharedPreferences.getData(sharedPre,"login_id"));
            }
        }
    }

    private void getUser(String id){
        progressDialog.show();
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance(apiBaseUrl).create(ApiInterface.class);

        Call<String> call = apiInterface.getUserById(String.valueOf(id));

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
                                AppSharedPreferences.saveData(sharedPre,"user_role", user.getUserType());
                                redirectToHome(user.getUserType());
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
                    progressDialog.dismiss();
                    try {
                        showDialog(context, "Opps...!", "Check your connection", ()->{});
                        String error = response.errorBody().string();
                        System.out.println("_==================Error: "+error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                showDialog(context, "Opps...!", "Check your connection", ()->{});
                progressDialog.dismiss();
                System.out.println("_==================Error! couln'd send the request ==================\n" + t.getMessage());
            }
        });
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