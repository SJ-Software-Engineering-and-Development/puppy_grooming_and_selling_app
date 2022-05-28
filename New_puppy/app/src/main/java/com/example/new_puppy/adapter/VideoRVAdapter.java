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
import com.example.new_puppy.fragment.VeterinaryFragment;
import com.example.new_puppy.fragment.YtbVideoFragment;
import com.example.new_puppy.model.YtbVideo;
import com.example.new_puppy.utils.ListItemAnimation;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class VideoRVAdapter extends RecyclerView.Adapter<VideoRVAdapter.UsersRecyclerviewHolder>{
    Context context;
    List<YtbVideo> dataList;
    List<YtbVideo> filteredDataList;

    public VideoRVAdapter(Context context, List<YtbVideo> userList) {
        this.context = context;
        this.dataList = userList;
        this.filteredDataList = userList;
    }

    @NonNull
    @Override
    public VideoRVAdapter.UsersRecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.video_recyclerview_item, parent, false);
        return new VideoRVAdapter.UsersRecyclerviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoRVAdapter.UsersRecyclerviewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txtTitle.setText(filteredDataList.get(position).getTitle());
        holder.cardSubTitle.setText("----");
        holder.bottomLText.setText(String.valueOf(filteredDataList.get(position).getLikes()));
        holder.bottomMText.setText(String.valueOf(filteredDataList.get(position).getViews()));
        holder.bottomRText.setText(String.valueOf(filteredDataList.get(position).getComments()));
        // Picasso.get().load(filteredDataList.get(position).getImageUrl()).into(holder.cardimage);
        // holder.cardimage.setImageResource(R.drawable.ic_baseline_local_hospital_24);

        ListItemAnimation.animateFadeIn(holder.itemView, position);

        // TODO: Item onClick
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YtbVideoFragment.listItemOnClick(filteredDataList.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredDataList.size();
    }

    public static final class UsersRecyclerviewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, cardSubTitle, bottomLText, bottomMText, bottomRText;

        public UsersRecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.cardTitle);
            cardSubTitle = itemView.findViewById(R.id.cardSubTitle);
            bottomLText = itemView.findViewById(R.id.bottomLText);
            bottomMText = itemView.findViewById(R.id.bottomMText);
            bottomRText = itemView.findViewById(R.id.bottomRText);
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

                    List<YtbVideo> lstFiltered = new ArrayList<>();
                    for (YtbVideo row : dataList) {
                        if (row.getTitle().toLowerCase().contains(Key.toLowerCase()) || row.getUrl().toLowerCase().contains(Key.toLowerCase()) ) {
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
                filteredDataList = (List<YtbVideo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
