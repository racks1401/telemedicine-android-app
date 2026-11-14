package com.racks.mydoctor.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.racks.mydoctor.R;
import com.racks.mydoctor.activities.AppointmentDetailActivity;
import com.racks.mydoctor.activities.PatientActivity;
import com.racks.mydoctor.models.AppointmentDetailModel;

import java.util.HashMap;

public class PaymentFragment extends Fragment implements View.OnClickListener{

    private ImageView back_button;
    private RelativeLayout rl_card_button, rl_upi_button;
    private Button pay_button;
    private RadioButton card_radio_button, upi_radio_button;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;
    private AppointmentDetailModel appointmentModel;
    private String paymentMode;
    private String tempAppointmentId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        back_button = view.findViewById(R.id.back_button);
        rl_card_button = view.findViewById(R.id.card_button);
        rl_upi_button = view.findViewById(R.id.upi_button);
        pay_button = view.findViewById(R.id.pay_button);
        card_radio_button = view.findViewById(R.id.card_radio_button);
        upi_radio_button = view.findViewById(R.id.upi_radio_button);
        dbRef = FirebaseDatabase.getInstance().getReference("Appointments");
        mAuth = FirebaseAuth.getInstance();
        appointmentModel = new ViewModelProvider(requireActivity()).get(AppointmentDetailModel.class);
        tempAppointmentId = appointmentModel.getAppointmentId();
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_book_appointment);
        back_button.setOnClickListener(v->navController.popBackStack());
        rl_card_button.setOnClickListener(this);
        rl_upi_button.setOnClickListener(this);
        pay_button.setOnClickListener(this);
        return view;
    }
    private void startPayProcess() {
        String uniqueId = dbRef.push().getKey();
        HashMap<Object, Object> appointmentData = new HashMap<>();
        appointmentData.put("patientName", appointmentModel.getPatientName());
        appointmentData.put("patientGender", appointmentModel.getPatientGender());
        appointmentData.put("patientAge", appointmentModel.getPatientAge());
        appointmentData.put("patientWeight", appointmentModel.getPatientWeight());
        appointmentData.put("patientBloodGroup", appointmentModel.getPatientBloodGroup());
        appointmentData.put("problemDesc", appointmentModel.getProblemDesc());
        appointmentData.put("appointmentMode", appointmentModel.getAppointmentMode());
        appointmentData.put("appointmentDate", appointmentModel.getAppointmentDate());
        appointmentData.put("appointmentTime", appointmentModel.getAppointmentTime());
        appointmentData.put("doctorId", appointmentModel.getDoctorId());
        appointmentData.put("doctorName", appointmentModel.getDoctorName());
        appointmentData.put("doctorSpeciality", appointmentModel.getDoctorSpeciality());
        appointmentData.put("doctorDegree", appointmentModel.getDoctorDegree());
        appointmentData.put("doctorProfileUrl", appointmentModel.getDoctorProfileUrl());
        appointmentData.put("patientId", mAuth.getUid());
        appointmentData.put("appointmentId", uniqueId);
        appointmentData.put("prescriptionFileUrl", appointmentModel.getPrescriptionFileUrl());
        appointmentData.put("appointmentCharge", appointmentModel.getAppointmentCharge());
        appointmentData.put("isCompleted", false);
        if (appointmentModel.getAppointmentMode().equals("Clinic Visit")){
            appointmentData.put("doctorClinicAddress", appointmentModel.getDoctorClinicAddress());
        }
        tempAppointmentId = uniqueId;
        if (uniqueId!=null){
            dbRef.child(uniqueId).setValue(appointmentData).addOnSuccessListener(unused -> showSuccessDialog())
                    .addOnFailureListener(e -> Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
    private void showSuccessDialog(){
        pay_button.setEnabled(true);
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_final_page, null);
        dialog.setContentView(dialogView);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        Button viewAppointmentDetailBtn = dialogView.findViewById(R.id.view_appointment_button);
        Button goToHomePageBtn = dialogView.findViewById(R.id.go_to_home_button);
        viewAppointmentDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(requireActivity(), AppointmentDetailActivity.class);
                intent.putExtra("appointmentId", tempAppointmentId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });
        goToHomePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(requireActivity(), PatientActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        dialog.show();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.card_button) {
            paymentMode = "Card";
            rl_card_button.setSelected(true);
            rl_upi_button.setSelected(false);
            card_radio_button.setChecked(true);
            upi_radio_button.setChecked(false);
        } else if (viewId == R.id.upi_button) {
            paymentMode = "UPI";
            rl_upi_button.setSelected(true);
            upi_radio_button.setChecked(true);
            rl_card_button.setSelected(false);
            card_radio_button.setChecked(false);
        }else{
            if (paymentMode!=null){
                pay_button.setEnabled(false);
                startPayProcess();
            }else {
                Toast.makeText(requireContext(), "Select Payment Methode", Toast.LENGTH_SHORT).show();
            }
        }
    }
}