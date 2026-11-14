package com.racks.mydoctor.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.racks.mydoctor.R;
import com.racks.mydoctor.activities.AppointmentDetailActivity;
import com.racks.mydoctor.models.AppointmentDetailModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.myViewHolder>{

    private final ArrayList<AppointmentDetailModel> modelArrayList;
    private final Context context;
    private final boolean isCompleted;

    public AppointmentAdapter(ArrayList<AppointmentDetailModel> modelArrayList, Context context, boolean isCompleted) {
        this.modelArrayList = modelArrayList;
        this.context = context;
        this.isCompleted = isCompleted;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_appointment_patient, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        AppointmentDetailModel appointmentModel = modelArrayList.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        holder.patient_name.setText(appointmentModel.getPatientName());
        holder.appointmentMode.setText(appointmentModel.getAppointmentMode());
        holder.appointmentDate.setText(dateFormat.format(appointmentModel.getAppointmentDate()));
        holder.appointmentTime.setText(appointmentModel.getAppointmentTime());
        if (isCompleted){
            holder.cancel_button.setVisibility(View.GONE);
        }else {
            holder.cancel_button.setVisibility(View.VISIBLE);
        }
        if (appointmentModel.getAppointmentMode().equals("Clinic Visit")){
            holder.modeIcon.setImageResource(R.drawable.ic_clinic_visit_24);
        } else if (appointmentModel.getAppointmentMode().equals("Messaging")) {
            holder.modeIcon.setImageResource(R.drawable.ic_chat_24);
        }else if (appointmentModel.getAppointmentMode().equals("Voice Call")){
            holder.modeIcon.setImageResource(R.drawable.ic_call_24);
        }else {
            holder.modeIcon.setImageResource(R.drawable.ic_video_24);
        }
        holder.show_detail_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AppointmentDetailActivity.class);
                intent.putExtra("appointmentId", appointmentModel.getAppointmentId());
//                Toast.makeText(context, appointmentModel.getAppointmentId(), Toast.LENGTH_SHORT).show();
                context.startActivity(intent);
            }
        });
        holder.cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAppointment(appointmentModel.getAppointmentId());
            }
        });
    }

    private void cancelAppointment(String appointmentId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Appointments");
        dbRef.child(appointmentId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, "Canceled", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder{
        ImageView profilePic, modeIcon;
        TextView patient_name, appointmentMode, appointmentDate, appointmentTime;
        LinearLayout show_detail_button, cancel_button;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.img_patient_profile);
            modeIcon = itemView.findViewById(R.id.img_icon_appointment_mode);
            patient_name = itemView.findViewById(R.id.tv_patient_name);
            appointmentMode = itemView.findViewById(R.id.tv_appointment_mode);
            appointmentDate = itemView.findViewById(R.id.tv_appointment_date);
            appointmentTime = itemView.findViewById(R.id.tv_appointment_time);
            show_detail_button = itemView.findViewById(R.id.ll_show_detail_button);
            cancel_button = itemView.findViewById(R.id.ll_cancel_button);
        }
    }
}
