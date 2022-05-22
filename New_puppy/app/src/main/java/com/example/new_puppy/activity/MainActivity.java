package com.example.new_puppy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.new_puppy.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    Spinner spinner;
    int currentItem = 0;
    ImageButton imageButton;

    List<String> menuItems = new ArrayList<>();

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButton = findViewById(R.id.Add_Post_Open);


        spinner = (Spinner)findViewById(R.id.main_spinner);

        menuItems = Arrays.asList(getResources().getStringArray(R.array.SpinnerMenu));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
            String mItem= menuItems.get(position);

          //  Toast.makeText(MainActivity.this, mItem, Toast.LENGTH_SHORT).show();

            if(currentItem==position){
                return; //do nothing
            }

            else {
                switch (menuItems.get(position)){
                    case "Home":
                        startActivity(new Intent(MainActivity.this,MainActivity.class));
                        break;
                    case "Education":
                        startActivity(new Intent(MainActivity.this,Education.class));
                        break;
                    case "Bath House":
                        startActivity(new Intent(MainActivity.this,BathHouse.class));
                        break;
                    case "Veterinarians":
                        startActivity(new Intent(MainActivity.this,Veterinarians.class));
                        break;
                    case "Bright Kennal":
                        startActivity(new Intent(MainActivity.this,Bright_Kennel.class));
                        break;
                    case "About":
                        startActivity(new Intent(MainActivity.this,About.class));
                        break;
                }


            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    });


imageButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        Intent intent = new Intent(getApplicationContext(), AddPost.class);
        startActivity(intent);





    }
});


    }
}