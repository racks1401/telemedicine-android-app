package com.racks.mydoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racks.mydoctor.R;
import com.racks.mydoctor.adapters.ReviewerRecyclerAdapter;
import com.racks.mydoctor.models.AppointmentDetailModel;
import com.racks.mydoctor.models.DoctorDetailModel;
import com.racks.mydoctor.models.ReviewerModel;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorDetailActivity extends AppCompatActivity {

    private ImageView img_back_button;
    private TextView tv_doctor_name_header, tv_doctor_name, tv_doctor_speciality,
            tv_doctor_degree, tv_about_doctor, tv_working_time;
    private CircleImageView img_doctor_profile;
    private Button book_appointment_button;
    private final ArrayList<ReviewerModel> reviewerModelArrayList = new ArrayList<>();
    private ReviewerRecyclerAdapter adapter;
    private String doctorId;
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child("Doctors");
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_detail);
        img_back_button = findViewById(R.id.img_back_button);
        img_doctor_profile = findViewById(R.id.img_doctor_profile);
        tv_doctor_name_header = findViewById(R.id.tv_doctor_name_header);
        tv_doctor_name = findViewById(R.id.tv_doctor_name);
        tv_doctor_speciality = findViewById(R.id.tv_doctor_speciality);
        tv_doctor_degree = findViewById(R.id.tv_doctor_degree);
        tv_about_doctor = findViewById(R.id.tv_about_doctor);
        tv_working_time = findViewById(R.id.tv_working_time);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        book_appointment_button = findViewById(R.id.book_appointment_button);
        adapter = new ReviewerRecyclerAdapter(reviewerModelArrayList, this);
        recyclerView.setAdapter(adapter);
        doctorId = getIntent().getStringExtra("doctorId");
        if (doctorId!=null) {
            setDataInView();
        }else{
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    private void setListeners() {
        img_back_button.setOnClickListener(v-> getOnBackPressedDispatcher().onBackPressed());
        book_appointment_button.setOnClickListener(v -> {
            Intent intent1 = new Intent(DoctorDetailActivity.this, BookAppointmentActivity.class);
            intent1.putExtra("doctorId", doctorId);
            startActivity(intent1);
            finish();
        });
    }

    private void setDataInView() {
        dbRef.child(doctorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    updateUi(snapshot);
                    setListeners();
                }else{
                    Toast.makeText(DoctorDetailActivity.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        dbRef.child(doctorId).child("Reviews").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()){
                    reviewerModelArrayList.clear();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        ReviewerModel reviewerModel = dataSnapshot.getValue(ReviewerModel.class);
                        reviewerModelArrayList.add(reviewerModel);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void updateUi(DataSnapshot snapshot){
        tv_doctor_name_header.setText(snapshot.child("name").getValue(String.class));
        tv_doctor_name.setText(snapshot.child("name").getValue(String.class));
        tv_doctor_speciality.setText(snapshot.child("specialization").getValue(String.class));
        tv_doctor_degree.setText(snapshot.child("qualification").getValue(String.class));
        tv_about_doctor.setText(snapshot.child("aboutMe").getValue(String.class));
        tv_working_time.setText(snapshot.child("working_time").getValue(String.class));

        if (Objects.equals(snapshot.child("gender").getValue(String.class), "Male")) {
            Glide.with(this)
                    .load(snapshot.child("profilePicUrl").getValue(String.class))
                    .placeholder(R.drawable.ic_doctor_male)
                    .into(img_doctor_profile);
        }else{
            Glide.with(this)
                    .load(snapshot.child("profilePicUrl").getValue(String.class))
                    .placeholder(R.drawable.ic_doctor_female)
                    .into(img_doctor_profile);
        }
    }

}