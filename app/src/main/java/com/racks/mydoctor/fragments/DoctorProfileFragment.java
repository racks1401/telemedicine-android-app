package com.racks.mydoctor.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.racks.mydoctor.R;
import com.racks.mydoctor.activities.EditProfileActivity;
import com.racks.mydoctor.activities.EditProfileDoctorActivity;
import com.racks.mydoctor.activities.LoginActivity;
import com.racks.mydoctor.helper.SharedPreferenceManager;
import com.racks.mydoctor.models.UserDetailModel;
import com.racks.mydoctor.models.UserDetailViewModel;

import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class DoctorProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private UserDetailViewModel userDetailViewModel;
    private Observer<UserDetailModel> userDetailObserver;
    private CircleImageView img_user_profile;
    private TextView tv_user_name, tv_account_setting;
    private TextView tv_user_email;
    private TextView tv_app_version;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);
        img_user_profile = view.findViewById(R.id.ig_profile);
        tv_user_name = view.findViewById(R.id.tv_user_name);
        tv_user_email = view.findViewById(R.id.tv_user_email);
        tv_account_setting = view.findViewById(R.id.tv_account_setting);
        TextView tv_logout_button = view.findViewById(R.id.logout_btn);
        tv_app_version = view.findViewById(R.id.tv_app_version);
        mAuth = FirebaseAuth.getInstance();
        userDetailViewModel = new ViewModelProvider(requireActivity()).get(UserDetailViewModel.class);
        updateUi();
        tv_logout_button.setOnClickListener(v -> {
            SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(requireContext());
            sharedPreferenceManager.clearUserTypeData();
            if (userDetailObserver != null) {
                userDetailViewModel.getUserDetail().removeObserver(userDetailObserver);
            }
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
        tv_account_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), EditProfileDoctorActivity.class));
            }
        });
        return view;
    }
    public static String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            return "null";
        }
    }

    private void updateUi() {
        tv_app_version.setText("version : "+getVersionName(requireContext()));
        userDetailObserver = userDetailModel -> {
            if (userDetailModel != null) {
                Date dobDate = new Date(userDetailModel.getDob());
                Calendar currentDate = Calendar.getInstance();
                Calendar dobCalendar = Calendar.getInstance();
                dobCalendar.setTime(dobDate);
                int ageYears = currentDate.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
                try {
                    if (userDetailModel.getGender() != null) {
                        String nameWithAge = userDetailModel.getName() + "(" + ageYears + "Y/" + (userDetailModel.getGender().charAt(0)) + ")";
                        tv_user_name.setText(nameWithAge);
                    }
                } catch (NullPointerException exception) {
                    Toast.makeText(requireContext(), "Update Profile Data", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(requireActivity(), EditProfileActivity.class));
                }
                tv_user_email.setText(userDetailModel.getEmail());
                Glide.with(requireContext())
                        .load(userDetailModel.getProfilePicUrl())
                        .placeholder(R.drawable.profile_placeholder)
                        .into(img_user_profile);
            } else {
                Toast.makeText(requireContext(), "Something went wrong please login again.", Toast.LENGTH_SHORT).show();
            }
        };

        userDetailViewModel.getUserDetail().observe(getViewLifecycleOwner(), userDetailObserver);

    }
}