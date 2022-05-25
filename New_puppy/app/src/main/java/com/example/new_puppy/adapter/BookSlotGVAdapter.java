package com.example.new_puppy.adapter;

import com.example.new_puppy.fragment.BookingSlotsFragment;
import com.example.new_puppy.model.BookingSlot;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import com.example.new_puppy.R;

public class BookSlotGVAdapter extends ArrayAdapter<BookingSlot> {
    public BookSlotGVAdapter(@NonNull Context context, ArrayList<BookingSlot> bookingSlotArrayList) {
        super(context, 0, bookingSlotArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.book_slot_item, parent, false);
        }
        BookingSlot bookingSlot = getItem(position);
        TextView txt_slot = listitemView.findViewById(R.id.txt_slot);
        CardView card = listitemView.findViewById(R.id.card);

        txt_slot.setText(bookingSlot.getFromTime() + " to " + bookingSlot.getToTime());

       if(!bookingSlot.isAvailable()){
           System.out.println("========================== NOT AVAILABLE ===================");
            card.setBackgroundColor(Color.parseColor("#E6BD00"));
            card.setClickable(false);
       }

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingSlotsFragment.slotOnclick(bookingSlot.getId(), bookingSlot.isAvailable(), bookingSlot.getFromTime() +" - " + bookingSlot.getToTime());
            }
        });

        return listitemView;
    }
}
