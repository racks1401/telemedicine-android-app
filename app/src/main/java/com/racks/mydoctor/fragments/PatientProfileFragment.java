package com.racks.mydoctor.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.racks.mydoctor.R;
import com.racks.mydoctor.activities.EditProfileActivity;
import com.racks.mydoctor.activities.LoginActivity;
import com.racks.mydoctor.databinding.FragmentPatientProfileBinding;
import com.racks.mydoctor.helper.SharedPreferenceManager;
import com.racks.mydoctor.models.UserDetailModel;
import com.racks.mydoctor.models.UserDetailViewModel;

import java.util.Calendar;
import java.util.Date;

public class PatientProfileFragment extends Fragment {

    private FragmentPatientProfileBinding binding;
    private FirebaseAuth mAuth;
    private UserDetailViewModel userDetailViewModel;
    private Observer<UserDetailModel> userDetailObserver;
    private String userType;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPatientProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        userDetailViewModel = new ViewModelProvider(requireActivity()).get(UserDetailViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        updateUi();

        binding.logoutBtn.setOnClickListener(v -> {
            SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(requireContext());
            sharedPreferenceManager.clearUserTypeData();
            if (userDetailObserver != null) {
                userDetailViewModel.getUserDetail().removeObserver(userDetailObserver);
            }
            goToLogin();

        });

        binding.tvAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), EditProfileActivity.class));
            }
        });
        binding.tvDeleteAccount.setOnClickListener(v -> {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            if (firebaseUser != null) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child("Patients");
                userRef.child(firebaseUser.getUid()).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        firebaseUser.delete().addOnCompleteListener(task1 -> {
                            Toast.makeText(requireContext(), "User Deleted Successfully.", Toast.LENGTH_SHORT).show();
                            goToLogin();
                        });
                    } else {
                        Toast.makeText(requireContext(), "Something went wrong, try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return root;
    }

    private void goToLogin() {
        // 1. Firebase logout
        mAuth.signOut();

        // 2. Google logout
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        googleSignInClient.signOut().addOnCompleteListener(task -> {

            // 3. Navigate to login screen
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
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
        binding.tvAppVersion.setText("version : "+getVersionName(requireContext()));
        userDetailObserver = userDetailModel -> {
            if (userDetailModel != null) {
                userType = userDetailModel.getUserType();
                Date dobDate = new Date(userDetailModel.getDob());
                Calendar currentDate = Calendar.getInstance();
                Calendar dobCalendar = Calendar.getInstance();
                dobCalendar.setTime(dobDate);
                int ageYears = currentDate.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
                try {
                    if (userDetailModel.getGender() != null) {
                        String nameWithAge = userDetailModel.getName() + "(" + ageYears + "Y/" + (userDetailModel.getGender().charAt(0)) + ")";
                        binding.tvUserName.setText(nameWithAge);
                    }
                } catch (NullPointerException exception) {
                    Toast.makeText(requireContext(), "Update Profile Data", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(requireActivity(), EditProfileActivity.class));
                }
                binding.tvUserEmail.setText(userDetailModel.getEmail());
                Glide.with(requireContext())
                        .load(userDetailModel.getProfilePicUrl())
                        .placeholder(R.drawable.profile_placeholder)
                        .into(binding.igProfile);
            } else {
                Toast.makeText(requireContext(), "Something went wrong please login again.", Toast.LENGTH_SHORT).show();
            }
        };

        userDetailViewModel.getUserDetail().observe(getViewLifecycleOwner(), userDetailObserver);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}