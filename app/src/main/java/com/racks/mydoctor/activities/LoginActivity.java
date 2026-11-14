package com.racks.mydoctor.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.racks.mydoctor.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initListener();

    }
    private void initListener() {

    }
}