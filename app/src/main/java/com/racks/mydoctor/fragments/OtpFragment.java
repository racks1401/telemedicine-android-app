package com.racks.mydoctor.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.racks.mydoctor.R;
import com.racks.mydoctor.activities.MainActivity;
import com.racks.mydoctor.activities.SignUpActivity;
import com.racks.mydoctor.helper.AuthHelper;
import com.racks.mydoctor.helper.SharedPreferenceManager;

import java.util.Timer;
import java.util.TimerTask;

public class OtpFragment extends Fragment {

    private EditText et_otp1, et_otp2, et_otp3, et_otp4, et_otp5, et_otp6;
    private TextView textView, tv_resend_timeout;
    private AppCompatButton verify_btn;
    private Long resendTimeout = 30L;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String verificationCode, phone;
    private View view1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_otp, container, false);
        phone = OtpFragmentArgs.fromBundle(getArguments()).getPhone();
        verificationCode = OtpFragmentArgs.fromBundle(getArguments()).getVerificationCode();
        et_otp1 = view.findViewById(R.id.et_otp1);
        et_otp2 = view.findViewById(R.id.et_otp2);
        et_otp3 = view.findViewById(R.id.et_otp3);
        et_otp4 = view.findViewById(R.id.et_otp4);
        et_otp5 = view.findViewById(R.id.et_otp5);
        et_otp6 = view.findViewById(R.id.et_otp6);
        textView = view.findViewById(R.id.textView);
        verify_btn = view.findViewById(R.id.verify_btn);
        tv_resend_timeout = view.findViewById(R.id.tv_resend_timeout);
        view1 = view;
        textView.setText("Enter OTP sent on " + phone);
        EditText[] otpEditText = new EditText[]{et_otp1, et_otp2, et_otp3, et_otp4, et_otp5, et_otp6};
        initTextWatcher(otpEditText);
        startResendTimer();
        verify_btn.setOnClickListener(v -> {
            StringBuilder otpBuilder = new StringBuilder();
            for (EditText editText : otpEditText) {
                if (editText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(requireContext(), "fill all boxes", Toast.LENGTH_SHORT).show();
                    return;
                }
                otpBuilder.append(editText.getText().toString().trim());
            }
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otpBuilder.toString());
            signIn(credential);
        });
        tv_resend_timeout.setOnClickListener(v -> {
            tv_resend_timeout.setText("Resending OTP Please wait...");
            sendOtp();
        });
        return view;
    }

    public static class GenericTextWatcher implements TextWatcher {
        private final EditText etPrev;
        private final EditText etNext;

        public GenericTextWatcher(EditText etPrev, EditText etNext) {
            this.etPrev = etPrev;
            this.etNext = etNext;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            if (text.length() == 1)
                etNext.requestFocus();
            else if (text.isEmpty())
                etPrev.requestFocus();
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    }

    private void initTextWatcher(EditText[] editTexts) {
        editTexts[0].addTextChangedListener(new GenericTextWatcher(editTexts[0], editTexts[1]));
        editTexts[1].addTextChangedListener(new GenericTextWatcher(editTexts[0], editTexts[2]));
        editTexts[2].addTextChangedListener(new GenericTextWatcher(editTexts[1], editTexts[3]));
        editTexts[3].addTextChangedListener(new GenericTextWatcher(editTexts[2], editTexts[4]));
        editTexts[4].addTextChangedListener(new GenericTextWatcher(editTexts[3], editTexts[5]));
        editTexts[5].addTextChangedListener(new GenericTextWatcher(editTexts[4], editTexts[5]));
    }

    private void sendOtp() {
        AuthHelper.authenticatePhoneNumber(requireActivity(), phone, false, new AuthHelper.PhoneAuthListener() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signIn(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String verificationId) {
                startResendTimer();
                verificationCode = verificationId;
                Toast.makeText(requireContext(), "OTP Sent Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                AuthHelper authHelper = new AuthHelper();
                if (task.isSuccessful()) {
                    authHelper.checkUserInDatabase(requireContext(), mAuth.getUid(), userType -> {
                        if (userType!=null) {
                            SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(requireContext());
                            sharedPreferenceManager.setUserType(userType);
                            startActivity(new Intent(requireActivity(), MainActivity.class));
                            requireActivity().finish();
                        } else {
                            startActivity(new Intent(requireActivity(), SignUpActivity.class));
                            requireActivity().finish();
                        }
                    });

                } else {
                    showToast(task.getException().toString());
                }
            }
        });
    }

    private void startResendTimer() {
        tv_resend_timeout.setEnabled(false);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                requireActivity().runOnUiThread(() -> tv_resend_timeout.setText("Resend OTP in " + resendTimeout + " seconds"));
                resendTimeout--;
                if (resendTimeout <= 0) {
                    resendTimeout = 30L;
                    timer.cancel();
                    requireActivity().runOnUiThread(() -> {
                        tv_resend_timeout.setText("Resend OTP");
                        tv_resend_timeout.setEnabled(true);
                    });
                }
            }
        }, 0, 1000);
    }

    private void showToast(String msg) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}