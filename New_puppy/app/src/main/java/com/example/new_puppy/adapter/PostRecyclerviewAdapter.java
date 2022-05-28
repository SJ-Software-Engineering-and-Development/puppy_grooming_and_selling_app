package com.example.new_puppy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.new_puppy.R;
import com.example.new_puppy.fragment.PostListingFragment;
import com.example.new_puppy.model.Post;
import com.example.new_puppy.utils.ListItemAnimation;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PostRecyclerviewAdapter extends RecyclerView.Adapter<PostRecyclerviewAdapter.UsersRecyclerviewHolder>{

    Context context;
    List<Post> dataList;
    List<Post> filteredDataList;

    public PostRecyclerviewAdapter(Context context, List<Post> userList) {
        this.context = context;
        this.dataList = userList;
        this.filteredDataList = userList;
    }

    @NonNull
    @Override
    public UsersRecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.post_recyclerview_item, parent, false);
        return new UsersRecyclerviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersRecyclerviewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.title.setText(filteredDataList.get(position).getTitle());
        holder.secondaryText.setText(filteredDataList.get(position).getDate());
        holder.bottomLText.setText(filteredDataList.get(position).getLocation());
        holder.bottomRText.setText("Rs."+filteredDataList.get(position).getPrice());
        holder.txtLevel.setText(filteredDataList.get(position).getGender());
        Picasso.get().load(filteredDataList.get(position).getImageUrl()).into(holder.cardimage);

        ListItemAnimation.animateFadeIn(holder.itemView, position);

        // TODO: Item onClick
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostListingFragment.listItemOnClick(filteredDataList.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredDataList.size();
    }

    public static final class UsersRecyclerviewHolder extends RecyclerView.ViewHolder {

        TextView title,secondaryText,bottomLText,bottomRText,txtLevel;
        ImageView cardimage;

        //        CircleImageView imageDisaster;
        public UsersRecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.cardTitle);
            secondaryText = itemView.findViewById(R.id.cardSecondText);
            bottomLText = itemView.findViewById(R.id.bottomLText);
            bottomRText = itemView.findViewById(R.id.bottomRText);
            txtLevel = itemView.findViewById(R.id.txtLevel);
            cardimage = itemView.findViewById(R.id.item_image);
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
                        if (row.getLocation().toLowerCase().contains(Key.toLowerCase()) || row.getTitle().toLowerCase().contains(Key.toLowerCase()) || row.getAge().toLowerCase().contains(Key.toLowerCase()) ) {
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
