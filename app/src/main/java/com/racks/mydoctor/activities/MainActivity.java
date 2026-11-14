package com.racks.mydoctor.activities;

import com.racks.mydoctor.helper.InsetsHelper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.racks.mydoctor.R;
import com.racks.mydoctor.helper.SharedPreferenceManager;
import com.racks.mydoctor.models.UserDetailModel;
import com.racks.mydoctor.models.UserDetailViewModel;

public class MainActivity extends AppCompatActivity {
    private UserDetailViewModel userDetailViewModel;
    private SharedPreferenceManager sharedPreferenceManager;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InsetsHelper.applySystemBarInsets(this);
        userDetailViewModel = new ViewModelProvider(this).get(UserDetailViewModel.class);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            if (!sharedPreferenceManager.isFirstLogin() && sharedPreferenceManager.getUserType() != null) {
                goToHomeActivity(sharedPreferenceManager.getUserType());
            } else {
                fetchUserData();
            }
        }

    }

    private void fetchUserData() {
        userDetailViewModel.fetchUserDetailFromFirebase(sharedPreferenceManager.getUserType(), new UserDetailViewModel.FetchUserCallback() {
            @Override
            public void onSuccess(UserDetailModel userDetail) {
                goToHomeActivity(userDetail.getUserType());
            }

            @Override
            public void onError(String errorMessage) {
                mAuth.signOut();

//                sharedPreferenceManager.setUserType(null);
//                sharedPreferenceManager.setFirstLogin(true);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void goToHomeActivity(String userType) {
        sharedPreferenceManager.setFirstLogin(false);
        if (userType.equals("Doctor")) {
            startActivity(new Intent(MainActivity.this, DoctorActivity.class));
            finish();
        } else {
            startActivity(new Intent(MainActivity.this, PatientActivity.class));
            finish();
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}