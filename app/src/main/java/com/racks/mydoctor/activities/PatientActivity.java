package com.racks.mydoctor.activities;

import com.racks.mydoctor.helper.InsetsHelper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.racks.mydoctor.R;
import com.racks.mydoctor.models.UserDetailModel;
import com.racks.mydoctor.models.UserDetailViewModel;

import java.util.Objects;

public class PatientActivity extends AppCompatActivity {

    private UserDetailViewModel userDetailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        InsetsHelper.applySystemBarInsets(this);

        BottomNavigationView bottomBar = findViewById(R.id.bottom_bar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_patient);
        NavigationUI.setupWithNavController(bottomBar, navController);
        userDetailViewModel = new ViewModelProvider(this).get(UserDetailViewModel.class);
        checkUser();
    }

    private void checkUser() {
        userDetailViewModel.fetchUserDetailFromFirebase("Patient", new UserDetailViewModel.FetchUserCallback() {
            @Override
            public void onSuccess(UserDetailModel userDetail) {
                if (!Objects.equals(userDetail.getUserType(), "Patient")) {
                    Toast.makeText(PatientActivity.this, "Something went wrong, Please login again.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PatientActivity.this, LoginActivity.class));
                    finish();
                }
            }
            @Override
            public void onError(String errorMessage) {

                Toast.makeText(PatientActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PatientActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}