package com.racks.mydoctor.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.racks.mydoctor.R;
import com.racks.mydoctor.activities.DoctorDetailActivity;
import com.racks.mydoctor.models.AppointmentDetailModel;
import com.racks.mydoctor.models.DoctorDetailModel;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.myViewHolder>{

    private ArrayList<DoctorDetailModel> arrayList;
    private final Context context;

    public DoctorListAdapter(ArrayList<DoctorDetailModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_doctor,parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        DoctorDetailModel doctorDetail = arrayList.get(position);
        holder.name.setText(doctorDetail.getName());
        holder.speciality.setText(doctorDetail.getSpecialization());
        String exp = "Exp: "+doctorDetail.getExperience();
        holder.experience.setText(exp);
        if ("Male".equals(doctorDetail.getGender())){
            Glide.with(context)
                    .load(doctorDetail.getProfilePicUrl())
                    .placeholder(R.drawable.ic_doctor_male)
                    .into(holder.img_doctor_profile_pic);
        }else {
            Glide.with(context)
                    .load(doctorDetail.getProfilePicUrl())
                    .placeholder(R.drawable.ic_doctor_female)
                    .into(holder.img_doctor_profile_pic);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DoctorDetailActivity.class);
            intent.putExtra("doctorId", doctorDetail.getDoctorId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder{

        private final TextView name;
        private ImageView img_doctor_profile_pic;
        private final TextView speciality;
        private final TextView experience;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_doctor_name);
            speciality = itemView.findViewById(R.id.tv_doctor_speciality);
            experience = itemView.findViewById(R.id.tv_doctor_experience);
            img_doctor_profile_pic = itemView.findViewById(R.id.img_doctor_profile_pic);
        }
    }

    public void updateList(ArrayList<DoctorDetailModel> newArrayList){
        this.arrayList = newArrayList;
        notifyDataSetChanged();
    }
}
