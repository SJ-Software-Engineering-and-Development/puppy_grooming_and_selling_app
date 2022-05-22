package com.example.new_puppy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.new_puppy.R;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

public class BathHouse extends AppCompatActivity {

    SliderView sliderView;
    int[] images = {R.drawable.bath001,
    R.drawable.bath002};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bath_house);

        sliderView = findViewById(R.id.Bath_image_Slider);

        IMGSliderAdapterBath imgSliderAdapterBath = new IMGSliderAdapterBath(images);

        sliderView.setSliderAdapter(imgSliderAdapterBath);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();

    }
}