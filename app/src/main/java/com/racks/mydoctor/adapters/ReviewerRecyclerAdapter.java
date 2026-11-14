package com.racks.mydoctor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.racks.mydoctor.R;
import com.racks.mydoctor.models.ReviewerModel;

import java.util.ArrayList;

public class ReviewerRecyclerAdapter extends RecyclerView.Adapter<ReviewerRecyclerAdapter.reviewerViewHolder> {

    private ArrayList<ReviewerModel> reviewerModelArrayList;
    private Context context;

    public ReviewerRecyclerAdapter(ArrayList<ReviewerModel> reviewerModelArrayList, Context context) {
        this.reviewerModelArrayList = reviewerModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public reviewerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_reviews, parent, false);
        return new reviewerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull reviewerViewHolder holder, int position) {
        ReviewerModel reviewerModel = reviewerModelArrayList.get(position);
        holder.name.setText(reviewerModel.getName());
        holder.message.setText(reviewerModel.getMessage());

        if (reviewerModel.getProfilePicUrl() != null) {
            Glide.with(context)
                    .load(reviewerModel.getProfilePicUrl())
                    .into(holder.profilePic);
        }
    }

    @Override
    public int getItemCount() {
        return reviewerModelArrayList.size();
    }

    public static class reviewerViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView name, message;

        public reviewerViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.img_reviewer_profile_pic);
            name = itemView.findViewById(R.id.tv_reviewer_name);
            message = itemView.findViewById(R.id.tv_reviewer_message);
        }
    }
}
