package com.example.new_puppy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.new_puppy.R;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Bath_House_Booking
        extends AppCompatActivity
implements AdapterView.OnItemSelectedListener {

    Calendar MyCalander;
    EditText editText;
    TimePicker TmPicker;
    Button Puppy_Booking_Book;
    Spinner booking_spinner;

    String  selectedbookingType="";
    String  selectedTime="";
    String  user_nic ="";

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

    private SharedPreferences sharedPre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bath_house_booking);

         sharedPre= getSharedPreferences("puppy_app", 0);
         user_nic =    AppSharedPreferences.getData(sharedPre, "user_nic");

         System.out.println("================NIC"+ user_nic);

        TmPicker = (TimePicker) findViewById(R.id.TimePicker);
        TmPicker.setIs24HourView(true);
        Puppy_Booking_Book = findViewById(R.id.Puppy_Booking_Book);
        booking_spinner= findViewById(R.id.booking_spinner);

        booking_spinner.setOnItemSelectedListener(this);
        //Adapter with items
        ArrayAdapter adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                bookingTypes
        );

        //set layout res for each item of spiner
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        booking_spinner.setAdapter(adapter);

        //For Calander
         MyCalander = Calendar.getInstance();

        editText = (EditText) findViewById(R.id.Booking_Receve_Date);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int Year, int MonthOfYear, int DateOfMonth) {
                MyCalander.set(Calendar.YEAR, Year);
                MyCalander.set(Calendar.MONTH, MonthOfYear);
                MyCalander.set(Calendar.DAY_OF_MONTH, DateOfMonth);
                updatetable();
            }
        };


        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(Bath_House_Booking.this, date, MyCalander.get(Calendar.YEAR), MyCalander.get(Calendar.MONTH),
                        MyCalander.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Puppy_Booking_Book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String PackageType=selectedbookingType,
                        Date=editText.getText().toString(),
                        NicNumber=user_nic;
                        String Time;

                int hour, minutes;
                String am_pm;

                if(Build.VERSION.SDK_INT>=23){
                    hour= TmPicker.getHour();
                    minutes= TmPicker.getMinute();
                }else{
                    hour= TmPicker.getCurrentHour();
                    minutes= TmPicker.getCurrentMinute();
                }

                if(hour > 12){
                    am_pm ="PM";
                    hour = hour - 12;
                }else{
                    am_pm="AM";
                }

                Time= hour + ":"+ minutes+" "+am_pm;

                System.out.println("selectedbookingType" + PackageType);
                System.out.println("Date " + Date);
                System.out.println("Time " + Time);
                System.out.println("user_nic" + NicNumber);

                if (!PackageType.equals("")
                        && !Date.equals("")
                        && !Time.equals("")
                        && !NicNumber.equals("")
                )
                {
                    //Start ProgressBar first (Set visibility VISIBLE)
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[4];

                            field[0] = "PackageType";
                            field[1] = "Date";
                            field[2] = "Time";
                            field[3] = "NicNumber";

                            //Creating array for data
                            String[] data = new String[4];
                            data[0] = PackageType;
                            data[1] = Date;
                            data[2] = Time;
                            data[3] = NicNumber;

                            PutData putData = new PutData("http://192.168.1.147/LoginRegister/Booking.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    //End ProgressBar (Set visibility to GONE)

                                    if(result.equals("Booking Success")){
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(getApplicationContext(), BathHouse.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),""+result,Toast.LENGTH_LONG).show();
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
    }

    private void updatetable(){
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editText.setText(sdf.format(MyCalander.getTime()));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        selectedbookingType = bookingTypes[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}




