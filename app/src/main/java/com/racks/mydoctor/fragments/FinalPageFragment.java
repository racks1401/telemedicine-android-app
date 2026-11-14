package com.racks.mydoctor.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racks.mydoctor.R;
import com.racks.mydoctor.models.AppointmentDetailModel;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class FinalPageFragment extends Fragment {
    private TextView tv_doctor_name, tv_doctor_speciality, tv_doctor_degree, tv_appointment_date,
            tv_appointment_time, tv_appointment_charge, tv_appointment_mode, tv_clinic_address, tv_problems_and_symptoms;
    private ImageView img_doctor_profile;
    private DatabaseReference dbRef;
    private AppointmentDetailModel appointmentDetailModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_final_page, container, false);
        tv_doctor_name = view.findViewById(R.id.tv_doctor_name);
        tv_doctor_speciality = view.findViewById(R.id.tv_doctor_speciality);
        tv_doctor_degree = view.findViewById(R.id.tv_doctor_degree);
        tv_appointment_date = view.findViewById(R.id.tv_appointment_date);
        tv_appointment_time = view.findViewById(R.id.tv_appointment_time);
        tv_appointment_charge = view.findViewById(R.id.tv_appointment_charge);
        tv_appointment_mode = view.findViewById(R.id.tv_appointment_mode);
        tv_clinic_address = view.findViewById(R.id.tv_clinic_address);
        img_doctor_profile = view.findViewById(R.id.img_doctor_profile);
        ImageView iv_back_btn = view.findViewById(R.id.iv_back_btn);
        tv_problems_and_symptoms = view.findViewById(R.id.tv_problems_and_symptoms);
        Button pay_and_book_btn = view.findViewById(R.id.pay_and_book_btn);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("Users");
        appointmentDetailModel = new ViewModelProvider(requireActivity()).get(AppointmentDetailModel.class);
        getSetData();
        NavDirections navDirections = FinalPageFragmentDirections.actionFinalPageFragmentToPaymentFragment();
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_book_appointment);
        pay_and_book_btn.setOnClickListener(v -> {
            navController.navigate(navDirections);
        });
        iv_back_btn.setOnClickListener(v-> navController.popBackStack());

        return view;
    }

    private void getSetData() {
        dbRef.child("Doctors").child(appointmentDetailModel.getDoctorId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String speciality = snapshot.child("specialization").getValue(String.class);
                    String degree = snapshot.child("qualification").getValue(String.class);
                    String profilePicUrl = snapshot.child("profilePicUrl").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);
                    tv_doctor_name.setText(name);
                    tv_doctor_speciality.setText(speciality);
                    tv_doctor_degree.setText(degree);
                    if (gender!=null){
                        if (gender.equals("Male")){
                            Glide.with(requireActivity())
                                    .load(profilePicUrl)
                                    .placeholder(R.drawable.ic_doctor_male)
                                    .into(img_doctor_profile);
                        }else {
                            Glide.with(requireActivity())
                                    .load(profilePicUrl)
                                    .placeholder(R.drawable.ic_doctor_female)
                                    .into(img_doctor_profile);
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String appointmentDateStr = dateFormat.format(appointmentDetailModel.getAppointmentDate());
        tv_appointment_date.setText(appointmentDateStr);
        tv_appointment_time.setText(appointmentDetailModel.getAppointmentTime());
        tv_appointment_mode.setText(appointmentDetailModel.getAppointmentMode());
        tv_problems_and_symptoms.setText(appointmentDetailModel.getProblemDesc());
        tv_appointment_charge.setText(appointmentDetailModel.getAppointmentCharge());
        if (Objects.equals(appointmentDetailModel.getAppointmentMode(), "Clinic Visit")) {
            tv_clinic_address.setText(appointmentDetailModel.getDoctorClinicAddress());
            tv_clinic_address.setVisibility(View.VISIBLE);
        } else {
            tv_appointment_charge.setText(appointmentDetailModel.getAppointmentCharge());
            tv_clinic_address.setVisibility(View.GONE);
        }

    }
}