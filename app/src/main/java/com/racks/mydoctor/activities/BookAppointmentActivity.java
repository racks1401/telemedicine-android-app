 package com.racks.mydoctor.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racks.mydoctor.R;
import com.racks.mydoctor.models.AppointmentDetailModel;

 public class BookAppointmentActivity extends AppCompatActivity {
     private AppointmentDetailModel appointmentDetailModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String doctorId = getIntent().getStringExtra("doctorId");
        appointmentDetailModel = new ViewModelProvider(this).get(AppointmentDetailModel.class);
        appointmentDetailModel.setDoctorId(doctorId);
        setContentView(R.layout.activity_book_appointment);
        fetchDoctorDetails(doctorId);

    }

     private void fetchDoctorDetails(String doctorId) {
         DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child("Doctors").child(doctorId);
         dbRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if (snapshot.exists() && snapshot.hasChildren()) {
                     appointmentDetailModel.setDoctorName(snapshot.child("name").getValue(String.class));
                     appointmentDetailModel.setDoctorSpeciality(snapshot.child("specialization").getValue(String.class));
                     appointmentDetailModel.setDoctorDegree(snapshot.child("qualification").getValue(String.class));
                     appointmentDetailModel.setDoctorProfileUrl(snapshot.child("profilePicUrl").getValue(String.class));
                     appointmentDetailModel.setDoctorClinicAddress(snapshot.child("clinicAddress").getValue(String.class));
                 }
                 else {
                     Toast.makeText(BookAppointmentActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
     }
 }