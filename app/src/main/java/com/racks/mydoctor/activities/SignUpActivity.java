package com.racks.mydoctor.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.racks.mydoctor.R;

public class SignUpActivity extends AppCompatActivity {
    private TextView tvHeader;
    private LinearProgressIndicator linearProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        tvHeader = findViewById(R.id.text_header);
        linearProgressBar = findViewById(R.id.linear_progress_indicator);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_sign_up);
        Bundle bundle = getIntent().getExtras();
        navController.navigate(R.id.signUpFragment1, bundle);

    }

    public void updateLinearProgress(int pageNo) {
        linearProgressBar.setProgress(25 * pageNo);
        if (pageNo == 1) {
            tvHeader.setText("Basic Details");
        } else if (pageNo == 2) {
            tvHeader.setText("Professional Detail");
        } else if (pageNo == 3) {
            tvHeader.setText("Availability");
        } else {
            tvHeader.setText("Additional Detail");

        }
    }
}