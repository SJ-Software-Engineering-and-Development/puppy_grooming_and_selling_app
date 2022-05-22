package com.example.new_puppy.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.new_puppy.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

public class IMGSliderAdapterBath extends SliderViewAdapter<IMGSliderAdapterBath.Holder>{

    int[] images;

    public IMGSliderAdapterBath(int[] images){

        this.images = images;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_bath_house,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {

      //  viewHolder.imageView.setImageResource(images[position]);


    }

    @Override
    public int getCount() {
        return images.length;
    }

    public class Holder extends SliderViewAdapter.ViewHolder{

        ImageView imageView;
        public Holder(View itemView){
            super(itemView);

            //TODO:
          //  imageView = itemView.findViewById(R.id.image_view);

        }


    }


}
