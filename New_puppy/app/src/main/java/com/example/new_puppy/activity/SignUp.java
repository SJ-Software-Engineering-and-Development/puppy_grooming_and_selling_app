package com.example.new_puppy.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.new_puppy.R;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    EditText editTextTextFullName, editTextTextNICNo, editTextTextCity, editTextTextUserType, editTextTextEmail,
            editTextContactNumber, editTextTextPassword, editTextTextReEnterPassword;
    TextView  SingUp, LoginText;
    Button singUpbutton;
    private Context context ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        context = this;
        editTextTextFullName = findViewById(R.id.editTextTextFullName);
        editTextTextNICNo = findViewById(R.id.editTextTextNICNo);
        editTextTextCity = findViewById(R.id.editTextTextCity);
        editTextTextUserType = findViewById(R.id.editTextTextUserType);
        editTextTextEmail = findViewById(R.id.editTextTextEmail);
        editTextContactNumber = findViewById(R.id.editTextContactNumber);
        editTextTextPassword = findViewById(R.id.editTextTextPassword);
        editTextTextReEnterPassword = findViewById(R.id.editTextTextReEnterPassword);

        SingUp = findViewById(R.id.SingUp);
        LoginText = findViewById(R.id.LoginText);
        singUpbutton = findViewById(R.id.singUpbutton);

        LoginText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(context,Login.class));
            }
        });

        singUpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname,idno,city,usertype,email,contactno,password,reenterpassword;

                fullname = String.valueOf(editTextTextFullName.getText());
                idno = String.valueOf(editTextTextNICNo.getText());
                city = String.valueOf(editTextTextCity.getText());
                usertype = String.valueOf(editTextTextUserType.getText());
                email = String.valueOf(editTextTextEmail.getText());
                contactno = String.valueOf(editTextContactNumber.getText());
                password = String.valueOf(editTextTextPassword.getText());
                reenterpassword = String.valueOf(editTextTextReEnterPassword.getText());

                if (!fullname.equals("")
                        && !idno.equals("")
                        && !city.equals("")
                        && !usertype.equals("")
                        && !email.equals("")
                        && !contactno.equals("")
                        && !password.equals("")
                       )
                {
                    //Start ProgressBar first (Set visibility VISIBLE)
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[7];
                            field[0] = "FullName";
                            field[1] = "NICNumber";
                            field[2] = "city";
                            field[3] = "usertype";
                            field[4] = "email";
                            field[5] = "contactno";
                            field[6] = "password";

                            //Creating array for data
                            String[] data = new String[7];
                            data[0] = fullname;
                            data[1] = idno;
                            data[2] = city;
                            data[3] = usertype;
                            data[4] = email;
                            data[5] = contactno;
                            data[6] = password;


                            PutData putData = new PutData("http://192.168.1.147/LoginRegister/signup.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    //End ProgressBar (Set visibility to GONE)


                                    if(result.equals("Sign Up Success")){

                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
//                                        Log.d("AAA",result);

                                        Toast.makeText(getApplicationContext(),""+result,Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                        startActivity(intent);
                                        finish();





                                    }

                                }
                            }
                            //End Write and Read data with URL
                        }
                    });

                }
                else {
                    Toast.makeText(getApplicationContext(),"ABC __All Fields are required", Toast.LENGTH_SHORT).show();

                }
    }
        });

        editTextContactNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
           @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if (validateMobile(editTextContactNumber.getText().toString()))
                {
                    singUpbutton.setEnabled(true);
                }
                else {
                    singUpbutton.setEnabled(false);
                    editTextContactNumber.setError("Invalid Mobile No");
                }

            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        editTextTextEmail.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                if (validateMobile(editTextTextEmail.getText().toString()))
//                {
//                    singUpbutton.setEnabled(true);
//                }
//                else {
//                    singUpbutton.setEnabled(false);
//                    editTextTextEmail.setError("Invalid Email");
//                }
//
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });





    }




    boolean validateMobile(String input){

        Pattern p = Pattern.compile("[0-9][0-9]{9}");
        Matcher m = p.matcher(input);
        return m.matches();
    }


//    boolean validateEmail(String input){
//
//        Pattern a = Pattern.compile("[a-z][A-Z][0-9][._-]+@[a-z]+\\\\.+[a-z+]");
//        Matcher b = a.matcher(input);
//        return b.matches();
//
//
//    }



















//
//    private Boolean validateEmail(){
//
//
//
//
//
//        String val = editTextTextEmail.getEditText().getText().toString();
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[]a-z+";
//
//        if (val.isEmpty()){
//            editTextTextEmail.setError("Field Can't Be Empty");
//            return false;
//
//        }
//        else if (!val.matches(emailPattern)){
//
//            editTextTextEmail.setError("Invalid Email Address");
//            return false;
//
//        }
//        else{
//            editTextTextEmail.setError(null);
//            return false;
//
//
//        }


    }








