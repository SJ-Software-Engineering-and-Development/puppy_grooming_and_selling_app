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
import com.example.new_puppy.fragment.UsersListFragment;
import com.example.new_puppy.fragment.VeterinaryFragment;
import com.example.new_puppy.model.Veterinary;
import com.example.new_puppy.utils.ListItemAnimation;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class VeterinaryRVAdapter extends RecyclerView.Adapter<VeterinaryRVAdapter.UsersRecyclerviewHolder>{
    Context context;
    List<Veterinary> dataList;
    List<Veterinary> filteredDataList;

    public VeterinaryRVAdapter(Context context, List<Veterinary> userList) {
        this.context = context;
        this.dataList = userList;
        this.filteredDataList = userList;
    }

    @NonNull
    @Override
    public VeterinaryRVAdapter.UsersRecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.admin_post_recycler_item, parent, false);
        return new VeterinaryRVAdapter.UsersRecyclerviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VeterinaryRVAdapter.UsersRecyclerviewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txtTitle.setText(filteredDataList.get(position).getTitle());
        holder.txtStatus.setText(filteredDataList.get(position).getCity());
        holder.txtDate.setText(filteredDataList.get(position).getContact());
        // Picasso.get().load(filteredDataList.get(position).getImageUrl()).into(holder.cardimage);
        holder.cardimage.setImageResource(R.drawable.ic_baseline_local_hospital_24);

        ListItemAnimation.animateFadeIn(holder.itemView, position);

        // TODO: Item onClick
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VeterinaryFragment.listItemOnClick(filteredDataList.get(position).getId());
            }
        });

        holder.btnActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VeterinaryFragment.listItemOnClick(filteredDataList.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredDataList.size();
    }

    public static final class UsersRecyclerviewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtStatus, txtDate;
        ImageView cardimage;
        MaterialButton btnActions;

        //        CircleImageView imageDisaster;
        public UsersRecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtDate = itemView.findViewById(R.id.txtDate);
            btnActions = itemView.findViewById(R.id.btnActions);
            cardimage = itemView.findViewById(R.id.imageItem);
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
                        if (row.getCity().toLowerCase().contains(Key.toLowerCase()) || row.getAddress().toLowerCase().contains(Key.toLowerCase()) ) {
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
