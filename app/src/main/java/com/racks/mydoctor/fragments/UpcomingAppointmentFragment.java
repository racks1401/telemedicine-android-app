package com.racks.mydoctor.fragments;

import com.racks.mydoctor.helper.SharedPreferenceManager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racks.mydoctor.R;
import com.racks.mydoctor.adapters.AppointmentAdapter;
import com.racks.mydoctor.models.AppointmentDetailModel;

import java.util.ArrayList;
import java.util.Objects;

public class UpcomingAppointmentFragment extends Fragment {

    private final ArrayList<AppointmentDetailModel> appointmentModelArrayList = new ArrayList<>();
    private AppointmentAdapter adapter;
    private DatabaseReference dbRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming_appointment, container, false);
        dbRef = FirebaseDatabase.getInstance().getReference("Appointments");
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new AppointmentAdapter(appointmentModelArrayList, requireContext(), false);
        recyclerView.setAdapter(adapter);
        getAllAppointments();
        return view;

    }

    private void getAllAppointments() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;
        String userType = new SharedPreferenceManager(requireContext()).getUserType();
        String idField = "Doctor".equals(userType) ? "doctorId" : "patientId";
        dbRef.orderByChild(idField).equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentModelArrayList.clear();
                if (snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (Objects.equals(dataSnapshot.child("doctorId").getValue(String.class), FirebaseAuth.getInstance().getUid()) ||
                                Objects.equals(dataSnapshot.child("patientId").getValue(String.class), FirebaseAuth.getInstance().getUid())){
                            if(Boolean.FALSE.equals(dataSnapshot.child("isCompleted").getValue(boolean.class))){
                                AppointmentDetailModel patientAppointmentModel = dataSnapshot.getValue(AppointmentDetailModel.class);
                                if (patientAppointmentModel != null) {
                                    patientAppointmentModel.setAppointmentId(dataSnapshot.getKey());
                                    appointmentModelArrayList.add(patientAppointmentModel);
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    adapter.notifyDataSetChanged();
                    if (isAdded()) Toast.makeText(requireContext(), "No Appointment yet..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}