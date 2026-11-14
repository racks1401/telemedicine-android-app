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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racks.mydoctor.R;
import com.racks.mydoctor.adapters.AppointmentAdapter;
import com.racks.mydoctor.adapters.DoctorListAdapter;
import com.racks.mydoctor.models.AppointmentDetailModel;
import com.racks.mydoctor.models.DoctorDetailModel;
import com.racks.mydoctor.models.DoctorDetailViewModel;
import com.racks.mydoctor.models.UserDetailModel;
import com.racks.mydoctor.models.UserDetailViewModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorHomeFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private MaterialSwitch switch_availability;
    private CircleImageView iv_user_profile_pic;
    private TextView tv_user_name;
    private AppointmentAdapter adapter;
    private UserDetailViewModel userDetailViewModel;
    private final ArrayList<AppointmentDetailModel> arrayList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_home, container, false);
        RecyclerView appointment_list_main = view.findViewById(R.id.recycler_upcoming_app_list);
        switch_availability = view.findViewById(R.id.switch_availability);
        iv_user_profile_pic = view.findViewById(R.id.iv_user_profile_pic);
        tv_user_name = view.findViewById(R.id.tv_user_name);
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("Appointments");
        adapter = new AppointmentAdapter(arrayList, requireContext(), false);
        appointment_list_main.setAdapter(adapter);
        userDetailViewModel = new ViewModelProvider(requireActivity()).get(UserDetailViewModel.class);
        fetchUserData();
        fetchAppointmentsData();
        return view;
    }

    private void fetchUserData() {
        userDetailViewModel.getUserDetail().observe(getViewLifecycleOwner(), new Observer<UserDetailModel>() {
            @Override
            public void onChanged(UserDetailModel userDetailModel) {
                if (userDetailModel!=null) {
                    switch_availability.setChecked(userDetailModel.isAvailable());
                    tv_user_name.setText(userDetailModel.getName());
                    Glide.with(requireContext())
                            .load(userDetailModel.getProfilePicUrl())
                            .placeholder(R.drawable.profile_placeholder)
                            .into(iv_user_profile_pic);
                }
            }
        });
    }

    private void fetchAppointmentsData() {
        if (mAuth.getUid() == null) return;
        dbRef.orderByChild("doctorId").equalTo(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    arrayList.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        AppointmentDetailModel appointmentDetailModel = snapshot1.getValue(AppointmentDetailModel.class);
                        if (appointmentDetailModel != null && !Boolean.TRUE.equals(snapshot1.child("isCompleted").getValue(Boolean.class))) {
                            appointmentDetailModel.setAppointmentId(snapshot1.getKey());
                            arrayList.add(appointmentDetailModel);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }else {
                    arrayList.clear();
                    adapter.notifyDataSetChanged();
                    if (isAdded()) Toast.makeText(requireContext(), "No Upcoming Appointment right now..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}