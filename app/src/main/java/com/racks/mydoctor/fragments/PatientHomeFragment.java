package com.racks.mydoctor.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racks.mydoctor.R;
import com.racks.mydoctor.adapters.DoctorListAdapter;
import com.racks.mydoctor.models.AppointmentDetailModel;
import com.racks.mydoctor.models.DoctorDetailModel;
import com.racks.mydoctor.models.UserDetailViewModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientHomeFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private CircleImageView iv_user_profile_pic;
    private TextView tv_user_name;
    private DoctorListAdapter doctorListAdapter;
    private UserDetailViewModel userDetailViewModel;
    private TextView tv_available_doc_home;
    private TextInputEditText et_search_doctor;
    private final ArrayList<DoctorDetailModel> doctorList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_home, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_doctor_list);
        tv_user_name = view.findViewById(R.id.tv_user_name);
        iv_user_profile_pic = view.findViewById(R.id.iv_user_profile_pic);
        et_search_doctor = view.findViewById(R.id.et_search_doctor);
        tv_available_doc_home = view.findViewById(R.id.tv_available_doc_home);
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("Users");
        doctorListAdapter = new DoctorListAdapter(doctorList, requireContext());
        recyclerView.setAdapter(doctorListAdapter);
        userDetailViewModel = new ViewModelProvider(requireActivity()).get(UserDetailViewModel.class);
        fetchUserData();
        fetchDoctorsList();
        clickListener();
        return view;
    }

    private void clickListener() {
        et_search_doctor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void updateList(String string) {
        ArrayList<DoctorDetailModel> filteredDoctorList = new ArrayList<>();
        for (DoctorDetailModel doctorDetailModel : doctorList){
            if (doctorDetailModel.getName() != null && doctorDetailModel.getName().toLowerCase().contains(string.toLowerCase().trim())){
                filteredDoctorList.add(doctorDetailModel);
            }
        }
        doctorListAdapter.updateList(filteredDoctorList);
        if(filteredDoctorList.isEmpty()){
            Toast.makeText(requireContext(), "No Result found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserData() {
        userDetailViewModel.getUserDetail().observe(getViewLifecycleOwner(), userDetailModel -> {
            if (userDetailModel!=null) {
                tv_user_name.setText(userDetailModel.getName());
                Glide.with(requireContext())
                        .load(userDetailModel.getProfilePicUrl())
                        .placeholder(R.drawable.profile_placeholder)
                        .into(iv_user_profile_pic);
            }
        });
    }

    private void fetchDoctorsList() {
        tv_available_doc_home.setText(R.string.fetching_doctors);
        dbRef.child("Doctors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    doctorList.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        DoctorDetailModel doctorDetail = snapshot1.getValue(DoctorDetailModel.class);
                        if (doctorDetail != null) {
                            doctorDetail.setDoctorId(snapshot1.getKey());
                            doctorList.add(doctorDetail);
                        }
                    }
                    doctorListAdapter.updateList(doctorList);
                    tv_available_doc_home.setText(R.string.available_doctors);
                } else {
                    tv_available_doc_home.setText(R.string.no_doctor_available_right_now);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tv_available_doc_home.setText(R.string.available_doctors);

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}