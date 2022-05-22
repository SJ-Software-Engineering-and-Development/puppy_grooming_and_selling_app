package com.example.new_puppy.activity;

import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.AbsActionBarView;

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
import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class Login extends AppCompatActivity {
    TextInputEditText textInputEditTextUsername,textInputEditTextPassword;
    Button buttonLogin,btnNewuserSignUp;
    Context context;
    TextView LogoName;

    private SharedPreferences sharedPre;

    Button buttoneka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context=this;

        sharedPre= getSharedPreferences("puppy_app", 0);

        textInputEditTextUsername = findViewById(R.id.txtLoginUserName);
        textInputEditTextPassword = findViewById(R.id.txtLoginPassword);
        btnNewuserSignUp = findViewById(R.id.btnNewuserSignUp);
        buttonLogin = findViewById(R.id.btnUserLogin);
        LogoName = findViewById(R.id.LogoName);

        buttoneka = findViewById(R.id.buttoneka);

        buttoneka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(context,Bath_House_Booking.class));

            }
        });

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

                            PutData putData = new PutData("http://192.168.1.100/LoginRegister/login.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {

                                    String result = putData.getResult();
                                    //End ProgressBar (Set visibility to GONE)

                                    if(result.equals("Login Success")){

                                        AppSharedPreferences.saveData(sharedPre,"user_nic", idno);

                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();

                                      //  Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        Intent intent = new Intent(getApplicationContext(), UserPageContainerActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        Log.d("AAA",result);
                                        Toast.makeText(getApplicationContext(),"##"+result,Toast.LENGTH_SHORT).show();

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


}