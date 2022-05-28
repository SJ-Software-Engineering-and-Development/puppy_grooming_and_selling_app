package com.example.new_puppy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.new_puppy.R;
import com.example.new_puppy.fragment.UserVeterinaryListFragment;
import com.example.new_puppy.fragment.UsersListFragment;
import com.example.new_puppy.fragment.VeterinaryFragment;
import com.example.new_puppy.model.Veterinary;
import com.example.new_puppy.utils.ListItemAnimation;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VeterinaryRVAdapter2 extends RecyclerView.Adapter<VeterinaryRVAdapter2.UsersRecyclerviewHolder>{
    Context context;
    List<Veterinary> dataList;
    List<Veterinary> filteredDataList;

    public VeterinaryRVAdapter2(Context context, List<Veterinary> userList) {
        this.context = context;
        this.dataList = userList;
        this.filteredDataList = userList;
    }

    @NonNull
    @Override
    public VeterinaryRVAdapter2.UsersRecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.veterinary_recyclerview_item, parent, false);
        return new VeterinaryRVAdapter2.UsersRecyclerviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VeterinaryRVAdapter2.UsersRecyclerviewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.title.setText(filteredDataList.get(position).getTitle());
        holder.secondaryText.setText(filteredDataList.get(position).getOpen_close_times());
        holder.bottomLText.setText(filteredDataList.get(position).getCity());
        holder.bottomRText.setText("");
        holder.txtLevel.setText(filteredDataList.get(position).getContact());
       // Picasso.get().load(filteredDataList.get(position).getImageUrl()).into(holder.cardimage);

        ListItemAnimation.animateFadeIn(holder.itemView, position);

        // TODO: Item onClick
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserVeterinaryListFragment.listItemOnClick(filteredDataList.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredDataList.size();
    }

    public static final class UsersRecyclerviewHolder extends RecyclerView.ViewHolder {

        TextView title,secondaryText,bottomLText,bottomRText,txtLevel;
        // ImageView cardimage;

        //        CircleImageView imageDisaster;
        public UsersRecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.cardTitle);
            secondaryText = itemView.findViewById(R.id.cardSecondText);
            bottomLText = itemView.findViewById(R.id.bottomLText);
            bottomRText = itemView.findViewById(R.id.bottomRText);
            txtLevel = itemView.findViewById(R.id.txtLevel);
          //  cardimage = itemView.findViewById(R.id.item_image);
        }
    }

    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String Key = charSequence.toString();
                if (Key.isEmpty()) {
                    filteredDataList = dataList;
                } else {

                    List<Veterinary> lstFiltered = new ArrayList<>();
                    for (Veterinary row : dataList) {
                        if (row.getTitle().toLowerCase().contains(Key.toLowerCase()) || row.getCity().toLowerCase().contains(Key.toLowerCase()) || row.getAddress().toLowerCase().contains(Key.toLowerCase()) ) {
                            lstFiltered.add(row);
                        }
                    }

                    filteredDataList = lstFiltered;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredDataList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredDataList = (List<Veterinary>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
