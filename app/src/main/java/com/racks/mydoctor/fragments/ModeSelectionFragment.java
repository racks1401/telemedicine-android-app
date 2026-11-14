package com.racks.mydoctor.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racks.mydoctor.R;
import com.racks.mydoctor.models.AppointmentDetailModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModeSelectionFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout rl_clinic_visit_button, rl_messaging_button, rl_voice_call_button, rl_video_call_button;
    private TextView tv_visitCharge, tv_messagingCharge, tv_voice_call_Charge, tv_video_call_Charge;
    private AppointmentDetailModel appointmentModel;
    private final ArrayList<String> modes = new ArrayList<>();
    private final ArrayList<String> charges = new ArrayList<>();
    private String mode;
    private String charge;
    private Button next_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mode_selection, container, false);
        rl_clinic_visit_button = view.findViewById(R.id.rl_clinic_visit_button);
        rl_messaging_button = view.findViewById(R.id.rl_messaging_button);
        rl_voice_call_button = view.findViewById(R.id.rl_voice_call_button);
        rl_video_call_button = view.findViewById(R.id.rl_video_call_button);
        tv_visitCharge = view.findViewById(R.id.price1);
        tv_messagingCharge = view.findViewById(R.id.price2);
        tv_voice_call_Charge = view.findViewById(R.id.price3);
        tv_video_call_Charge = view.findViewById(R.id.price4);
        next_button = view.findViewById(R.id.next_button);
        appointmentModel = new ViewModelProvider(requireActivity()).get(AppointmentDetailModel.class);
        fetchModesFromServer();
        return view;


    }

    private void fetchModesFromServer() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child("Doctors").child(appointmentModel.getDoctorId())
                .child("consultationModesAndCharges");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        modes.add(dataSnapshot.getKey());
                        charges.add(dataSnapshot.getValue(String.class));
                    }
                    showAvailableMode();
                    setClickListener();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setClickListener() {
        rl_clinic_visit_button.setOnClickListener(this);
        rl_messaging_button.setOnClickListener(this);
        rl_voice_call_button.setOnClickListener(this);
        rl_video_call_button.setOnClickListener(this);
        next_button.setOnClickListener(this);
    }

    private void showAvailableMode() {
        if (modes.contains("Clinic Visit")){
            rl_clinic_visit_button.setVisibility(View.VISIBLE);
            int index = modes.indexOf("Clinic Visit");
            tv_visitCharge.setText(charges.get(index));
        }else{
            rl_clinic_visit_button.setVisibility(View.GONE);
        }
        if (modes.contains("Messaging")){
            rl_messaging_button.setVisibility(View.VISIBLE);
            int index = modes.indexOf("Messaging");
            tv_messagingCharge.setText(charges.get(index));
        }else{
            rl_messaging_button.setVisibility(View.GONE);
        }
        if (modes.contains("Voice Call")){
            rl_voice_call_button.setVisibility(View.VISIBLE);
            int index = modes.indexOf("Voice Call");
            tv_voice_call_Charge.setText(charges.get(index));
        }else{
            rl_voice_call_button.setVisibility(View.GONE);
        }
        if (modes.contains("Video Call")){
            rl_video_call_button.setVisibility(View.VISIBLE);
            int index = modes.indexOf("Video Call");
            tv_video_call_Charge.setText(charges.get(index));
        }else{
            rl_video_call_button.setVisibility(View.GONE);
        }
    }
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.rl_clinic_visit_button) {
            mode = "Clinic Visit";
            int index = modes.indexOf(mode);
            charge = charges.get(index);
            rl_clinic_visit_button.setSelected(true);
            rl_messaging_button.setSelected(false);
            rl_voice_call_button.setSelected(false);
            rl_video_call_button.setSelected(false);
        } else if (viewId == R.id.rl_messaging_button) {
            mode = "Messaging";
            int index = modes.indexOf(mode);
            charge = charges.get(index);
            rl_clinic_visit_button.setSelected(false);
            rl_messaging_button.setSelected(true);
            rl_voice_call_button.setSelected(false);
            rl_video_call_button.setSelected(false);
        }
        else if (viewId == R.id.rl_voice_call_button) {
            mode = "Voice Call";
            int index = modes.indexOf(mode);
            charge = charges.get(index);
            rl_clinic_visit_button.setSelected(false);
            rl_messaging_button.setSelected(false);
            rl_voice_call_button.setSelected(true);
            rl_video_call_button.setSelected(false);
        }else if (viewId == R.id.rl_video_call_button) {
            mode = "Video Call";
            int index = modes.indexOf(mode);
            charge = charges.get(index);
            rl_clinic_visit_button.setSelected(false);
            rl_messaging_button.setSelected(false);
            rl_voice_call_button.setSelected(false);
            rl_video_call_button.setSelected(true);
        } else {
            if (mode!=null && charge!=null) {
                appointmentModel.setAppointmentMode(mode);
                appointmentModel.setAppointmentCharge(charge);
                NavDirections action = ModeSelectionFragmentDirections.actionModeSelectionFragmentToFinalPageFragment();
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_book_appointment);
                navController.navigate(action);
            }else{
                Toast.makeText(requireContext(), "Please Select mode first", Toast.LENGTH_SHORT).show();
            }
        }
    }
}