package com.racks.mydoctor.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racks.mydoctor.R;
import com.racks.mydoctor.activities.LoginActivity;
import com.racks.mydoctor.activities.MainActivity;
import com.racks.mydoctor.activities.SignUpActivity;
import com.racks.mydoctor.helper.SharedPreferenceManager;
import com.racks.mydoctor.models.DoctorDetailModel;
import com.racks.mydoctor.models.UserDetailModel;
import com.racks.mydoctor.models.UserDetailModel2;

public class SignUpFragment4 extends Fragment {
    private TextView tv_term_condition;
    private CheckBox checkbox_term_condition;
    private MaterialButton btn_next4;
    private DoctorDetailModel doctorDetailModel;
    private UserDetailModel2 userDetailModel2;
    private String userType;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up4, container, false);

        tv_term_condition = view.findViewById(R.id.tv_term_condition);
        checkbox_term_condition = view.findViewById(R.id.checkbox_term_condition);
        btn_next4 = view.findViewById(R.id.btn_next4);
        btn_next4.setEnabled(false);
        doctorDetailModel = new ViewModelProvider(requireActivity()).get(DoctorDetailModel.class);
        userDetailModel2 = new ViewModelProvider(requireActivity()).get(UserDetailModel2.class);
        userType = doctorDetailModel.getUserType();
        if (userType == null) {
            userType = userDetailModel2.getUserType();
        }
        dbRef.child("terms_and_conditions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String textStr = snapshot.child(userType).getValue(String.class);
                    tv_term_condition.setText(textStr);
                    btn_next4.setEnabled(true);
                } else {
                    tv_term_condition.setText("Error while fetching terms and conditions, you can proceed..");
                    btn_next4.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btn_next4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox_term_condition.isChecked()) {
                    createProfile();
                } else {
                    Toast.makeText(requireContext(), "Please Accept Terms and Conditions", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void createProfile() {
        if (userType.equals("Doctor")) {
            doctorDetailModel.setTermsAccepted(true);
            if (mAuth.getCurrentUser() != null && mAuth.getUid() != null) {
                dbRef.child("Users").child(userType + "s").child(mAuth.getUid()).setValue(doctorDetailModel).addOnSuccessListener(unused -> {
                            SharedPreferenceManager preferenceManager = new SharedPreferenceManager(requireContext());
                            preferenceManager.setUserType(doctorDetailModel.getUserType());
                            Log.d("SignUpFragment4 ", doctorDetailModel.getUserType() + doctorDetailModel.isTermsAccepted());
                            Toast.makeText(requireContext(), "All Done", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(requireActivity(), MainActivity.class));
                            requireActivity().finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(requireContext(), "Login Error, Please Login Again", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireActivity(), LoginActivity.class));
                requireActivity().finish();
            }

        } else {
            userDetailModel2.setTermsAccepted(true);
            if (mAuth.getCurrentUser() != null && mAuth.getUid() != null) {
                dbRef.child("Users").child(userType + "s").child(mAuth.getUid()).setValue(userDetailModel2).addOnSuccessListener(unused -> {
                            SharedPreferenceManager preferenceManager = new SharedPreferenceManager(requireContext());
                            preferenceManager.setUserType(userType);
                            Toast.makeText(requireContext(), "All Done", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(requireActivity(), MainActivity.class));
                            requireActivity().finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(requireContext(), "Login Error, Please Login Again", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireActivity(), LoginActivity.class));
                requireActivity().finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((SignUpActivity) requireActivity()).updateLinearProgress(4);
    }
}