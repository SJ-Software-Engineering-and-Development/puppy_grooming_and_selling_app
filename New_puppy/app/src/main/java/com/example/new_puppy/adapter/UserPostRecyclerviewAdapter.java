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
import com.example.new_puppy.fragment.UserPostListFragment;
import com.example.new_puppy.model.Post;
import com.example.new_puppy.utils.ListItemAnimation;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class UserPostRecyclerviewAdapter extends RecyclerView.Adapter<UserPostRecyclerviewAdapter.UsersRecyclerviewHolder>{
    Context context;
    List<Post> dataList;
    List<Post> filteredDataList;

    public UserPostRecyclerviewAdapter(Context context, List<Post> userList) {
        this.context = context;
        this.dataList = userList;
        this.filteredDataList = userList;
    }

    @NonNull
    @Override
    public UserPostRecyclerviewAdapter.UsersRecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.admin_post_recycler_item, parent, false);
        return new UserPostRecyclerviewAdapter.UsersRecyclerviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostRecyclerviewAdapter.UsersRecyclerviewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txtTitle.setText(filteredDataList.get(position).getTitle());
        holder.txtStatus.setText(filteredDataList.get(position).getStatus().toString());
        holder.txtDate.setText(filteredDataList.get(position).getDate());
        // Picasso.get().load(filteredDataList.get(position).getImageUrl()).into(holder.cardimage);
        holder.cardimage.setImageResource(R.drawable.ic_baseline_newspaper_yellow_24);

        ListItemAnimation.animateFadeIn(holder.itemView, position);

        // TODO: Item onClick
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserPostListFragment.listItemOnClick(filteredDataList.get(position).getId());
            }
        });

        holder.btnActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserPostListFragment.listItemOnClick(filteredDataList.get(position).getId());
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

                    List<Post> lstFiltered = new ArrayList<>();
                    for (Post row : dataList) {
                        if (row.getTitle().toLowerCase().contains(Key.toLowerCase()) || row.getAge().toLowerCase().contains(Key.toLowerCase()) ) {
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
                filteredDataList = (List<Post>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}