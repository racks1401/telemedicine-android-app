package com.racks.mydoctor.activities;

import com.racks.mydoctor.helper.InsetsHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racks.mydoctor.R;
import com.racks.mydoctor.models.AppointmentDetailModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentDetailActivity extends AppCompatActivity {
    private TextView tv_doctor_name, tv_doctor_speciality, tv_doctor_degree, tv_patient_name, tv_patient_age, tv_appointment_charge,
            tv_patient_gender, tv_appointment_date, tv_appointment_time, tv_appointment_mode, tv_clinic_address;
    private DatabaseReference dbRef;
    private CircleImageView img_doctor_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);
        InsetsHelper.applySystemBarInsets(this);

        tv_doctor_name = findViewById(R.id.tv_doctor_name);
        tv_doctor_speciality = findViewById(R.id.tv_doctor_speciality);
        tv_doctor_degree = findViewById(R.id.tv_doctor_degree);
        tv_patient_name = findViewById(R.id.tv_patient_name);
        tv_patient_age = findViewById(R.id.tv_patient_age);
        tv_patient_gender = findViewById(R.id.tv_patient_gender);
        tv_appointment_date = findViewById(R.id.tv_appointment_date);
        tv_appointment_time = findViewById(R.id.tv_appointment_time);
        tv_appointment_charge = findViewById(R.id.tv_appointment_charge);
        tv_appointment_mode = findViewById(R.id.tv_appointment_mode);
        tv_clinic_address = findViewById(R.id.tv_clinic_address);
        img_doctor_profile = findViewById(R.id.img_doctor_profile);
        Button button = findViewById(R.id.button2);
        dbRef = FirebaseDatabase.getInstance().getReference("Appointments");
        Intent intent = getIntent();
        String appointmentId = intent.getStringExtra("appointmentId");
        if (appointmentId!=null){
            fetchAppointmentDetails(appointmentId);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void fetchAppointmentDetails(String appointmentId) {
        dbRef.child(appointmentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()){
                    AppointmentDetailModel appointmentDetailModel = snapshot.getValue(AppointmentDetailModel.class);
                    setDataInView(appointmentDetailModel);
                }else {
                    Toast.makeText(AppointmentDetailActivity.this, "Appointment doesn't exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setDataInView(AppointmentDetailModel appointmentDetailModel) {
       if (appointmentDetailModel!=null){
           tv_doctor_name.setText(appointmentDetailModel.getDoctorName());
           tv_doctor_speciality.setText(appointmentDetailModel.getDoctorSpeciality());
           tv_doctor_degree.setText(appointmentDetailModel.getDoctorDegree());
           tv_patient_name.setText(appointmentDetailModel.getPatientName());
           tv_patient_gender.setText(appointmentDetailModel.getPatientGender());
           tv_patient_age.setText(appointmentDetailModel.getPatientAge());
           SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
           tv_appointment_date.setText(dateFormat.format(new Date(appointmentDetailModel.getAppointmentDate())));
           tv_appointment_time.setText(appointmentDetailModel.getAppointmentTime());
           tv_appointment_mode.setText(appointmentDetailModel.getAppointmentMode());
           tv_appointment_charge.setText(appointmentDetailModel.getAppointmentCharge());
           if (appointmentDetailModel.getDoctorClinicAddress()!=null && !Objects.equals(appointmentDetailModel.getDoctorClinicAddress(), "")){
               tv_clinic_address.setText(appointmentDetailModel.getDoctorClinicAddress());
               ((View) tv_clinic_address.getParent()).setVisibility(View.VISIBLE);
           }else{
               ((View) tv_clinic_address.getParent()).setVisibility(View.GONE);
           }
           if (appointmentDetailModel.getDoctorProfileUrl()!=null){
               Glide.with(this)
                       .load(appointmentDetailModel.getDoctorProfileUrl())
                       .into(img_doctor_profile);
           }

       }
    }
}