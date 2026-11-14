package com.racks.mydoctor.helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class AuthHelper {
    private static final String TAG = "PhoneAuthHelper";
    private static PhoneAuthProvider.ForceResendingToken resendingToken;

    public interface PhoneAuthListener {
        void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential);

        void onVerificationFailed(String errorMessage);

        void onCodeSent(String verificationId);
    }

    public static void authenticatePhoneNumber(final Activity activity, String phoneNumber,
                                               boolean isResend, final PhoneAuthListener listener) {
        PhoneAuthOptions.Builder builder = new PhoneAuthOptions.Builder(FirebaseAuth.getInstance())
                .setPhoneNumber(phoneNumber)
                .setTimeout(30L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        listener.onVerificationCompleted(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        listener.onVerificationFailed(e.getLocalizedMessage());
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        listener.onCodeSent(s);
                        resendingToken = forceResendingToken;
                    }
                });

        if (isResend)
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        else
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
    }

    public AuthHelper() {

    }

    public interface OnUserCheckListener {
        void onUserCheckComplete(String userType);
    }

    public void checkUserInDatabase(Context context, String uid, OnUserCheckListener listener) {
        DatabaseReference patientRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Patients").child(uid);
        DatabaseReference doctorRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Doctors").child(uid);

        doctorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(context);
                    sharedPreferenceManager.setUserType("Doctor");
                    Log.d("AuthHelper ", sharedPreferenceManager.getUserType());
                    listener.onUserCheckComplete("Doctor");
                } else {
                    patientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(context);
                                sharedPreferenceManager.setUserType("Patient");
                                Log.d("AuthHelper ", sharedPreferenceManager.getUserType());
                                listener.onUserCheckComplete("Patient");
                            } else {
                                listener.onUserCheckComplete(null);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            listener.onUserCheckComplete(null);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onUserCheckComplete(null);
            }
        });
    }

}
