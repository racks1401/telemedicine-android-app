package com.racks.mydoctor.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.racks.mydoctor.R;
import com.racks.mydoctor.activities.SignUpActivity;
import com.racks.mydoctor.models.DoctorDetailModel;

import java.util.ArrayList;

public class SignUpFragment2 extends Fragment {

    private TextInputLayout et_medical_reg_no, et_experience, et_bio;
    private AutoCompleteTextView autoComplete_qualification, autoComplete_specialization;
    DoctorDetailModel doctorDetailModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up2, container, false);
        et_medical_reg_no = view.findViewById(R.id.et_medical_reg_no);
        et_experience = view.findViewById(R.id.et_experience);
        et_bio = view.findViewById(R.id.et_bio);
        autoComplete_qualification = view.findViewById(R.id.autoComplete_qualification);
        autoComplete_specialization = view.findViewById(R.id.autoComplete_specialization);
        MaterialButton next_button = view.findViewById(R.id.btn_next2);
        doctorDetailModel = new ViewModelProvider(requireActivity()).get(DoctorDetailModel.class);
        String[] qualificationArray = getResources().getStringArray(R.array.autocomplete_items_qualification);
        String[] specializationArray = getResources().getStringArray(R.array.autocomplete_items_specialization);

        ArrayAdapter<String> qualificationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, qualificationArray);
        ArrayAdapter<String> specializationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, specializationArray);

        autoComplete_qualification.setAdapter(qualificationAdapter);
        autoComplete_qualification.setThreshold(1);
        autoComplete_specialization.setAdapter(specializationAdapter);
        autoComplete_specialization.setThreshold(1);


        next_button.setOnClickListener(v -> {
            String med_registration_no = et_medical_reg_no.getEditText().getText().toString().trim();
            String qualification = autoComplete_qualification.getText().toString().trim();
            String specialization = autoComplete_specialization.getText().toString().trim();
            String experience = et_experience.getEditText().getText().toString().trim();
            String bio = et_bio.getEditText().getText().toString().trim();
            if(med_registration_no.isEmpty()){
                et_medical_reg_no.setError("Field cannot be empty");
                et_medical_reg_no.requestFocus();
                return;
            }
            if (qualification.isEmpty()){
                autoComplete_qualification.setError("Field cannot be empty");
                autoComplete_qualification.requestFocus();
                return;

            }if (specialization.isEmpty()){
                autoComplete_specialization.setError("Field cannot be empty");
                autoComplete_specialization.requestFocus();
                return;
            }
            if (experience.isEmpty()){
                et_experience.setError("Field cannot be empty");
                et_experience.requestFocus();
                return;
            }
            if (bio.isEmpty()){
                Toast.makeText(requireContext(), "You haven't add bio, you can add it later", Toast.LENGTH_SHORT).show();
            }
            doctorDetailModel.setMedical_reg_no(med_registration_no);
            doctorDetailModel.setQualification(qualification);
            doctorDetailModel.setSpecialization(specialization);
            doctorDetailModel.setExperience(experience+" years");
            doctorDetailModel.setAboutMe(bio);
            NavDirections action = SignUpFragment2Directions.actionSignUpFragment2ToSignUpFragment3();
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_sign_up);
            navController.navigate(action);
        });

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((SignUpActivity) requireActivity()).updateLinearProgress(2);
    }
}