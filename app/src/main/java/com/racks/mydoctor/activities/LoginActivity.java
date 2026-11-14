package com.racks.mydoctor.activities;

import com.racks.mydoctor.helper.InsetsHelper;

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
        InsetsHelper.applySystemBarInsets(this);
        initListener();

    }
    private void initListener() {

    }
}