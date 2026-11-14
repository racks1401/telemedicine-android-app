package com.racks.mydoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racks.mydoctor.R;
import com.racks.mydoctor.helper.SharedPreferenceManager;
import com.racks.mydoctor.models.DoctorDetailModel;
import com.racks.mydoctor.models.DoctorDetailViewModel;
import com.racks.mydoctor.models.UserDetailModel;
import com.racks.mydoctor.models.UserDetailViewModel;

import java.util.Objects;

public class DoctorActivity extends AppCompatActivity {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");

    private  UserDetailViewModel userDetailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        BottomNavigationView bottomBar = findViewById(R.id.bottom_bar2);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_doctor);
        NavigationUI.setupWithNavController(bottomBar, navController);
        userDetailViewModel = new ViewModelProvider(this).get(UserDetailViewModel.class);
        checkUser();
    }
    private void checkUser() {
        userDetailViewModel.fetchUserDetailFromFirebase("Doctor", new UserDetailViewModel.FetchUserCallback() {
            @Override
            public void onSuccess(UserDetailModel userDetail) {
                if (!Objects.equals(userDetail.getUserType(), "Doctor")) {
                    Toast.makeText(DoctorActivity.this, "Something went wrong, Please login again.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DoctorActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(DoctorActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DoctorActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}
