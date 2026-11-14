package com.racks.mydoctor.fragments;

import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.racks.mydoctor.R;
import com.racks.mydoctor.activities.MainActivity;
import com.racks.mydoctor.activities.SignUpActivity;
import com.racks.mydoctor.helper.AuthHelper;
import com.racks.mydoctor.helper.SharedPreferenceManager;
import com.racks.mydoctor.helper.VisibilityWithFadeEffect;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CircularProgressIndicator signIn_progressBar2;
    private String verificationCode, phone;
    private TextInputLayout textInputLayout_phone, textInputLayout_pass;
    private TextInputEditText et_login_email, et_login_pass, et_login_phone;
    private View view1;
    private Button login_btn;
    private LinearLayout ll_login_email_pass;
    private TextView phone_login_btn, google_signIn_btn, tv_btn_forgot_password;
    private boolean isPhoneLogin = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        et_login_email = view.findViewById(R.id.et_login_email);
        et_login_pass = view.findViewById(R.id.et_login_pass);
        et_login_phone = view.findViewById(R.id.et_login_phone);
        textInputLayout_pass = view.findViewById(R.id.textInputLayout_pass);
        textInputLayout_phone = view.findViewById(R.id.textInputLayout_phone);
        ll_login_email_pass = view.findViewById(R.id.ll_login_email_pass);
        login_btn = view.findViewById(R.id.login_btn);
        phone_login_btn = view.findViewById(R.id.phone_login_btn);
        tv_btn_forgot_password = view.findViewById(R.id.tv_btn_forgot_password);
        google_signIn_btn = view.findViewById(R.id.google_signIn_btn);
        signIn_progressBar2 = view.findViewById(R.id.signIn_progressBar2);
        view1 = view;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        clickListeners();

        return view;
    }

    private void clickListeners() {
        tv_btn_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the custom layout
                LayoutInflater inflater = LayoutInflater.from(requireContext());
                View customView = inflater.inflate(R.layout.dialog_pass_reset, null);

                // Initialize views inside the custom layout
                TextInputEditText etInput = customView.findViewById(R.id.et_reset_email);
                Button btn_reset = customView.findViewById(R.id.btn_reset);

                // Create the dialog
                AlertDialog dialog = new AlertDialog.Builder(requireContext())
                        .setView(customView)
                        .setCancelable(true)
                        .create();

                btn_reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = etInput.getText().toString().trim();
                        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() ){
                            Toast.makeText(requireContext(), "Enter valid email", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        btn_reset.setEnabled(false);
                        btn_reset.setText(R.string.sending_please_wait);
                        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    btn_reset.setEnabled(true);
                                    btn_reset.setText(R.string.send_reset_link);
                                    Toast.makeText(requireContext(),"Password reset email sent", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }else {
                                    Toast.makeText(requireContext(), "Error: "+task.getException(), Toast.LENGTH_SHORT).show();
                                    btn_reset.setEnabled(true);
                                    btn_reset.setText(R.string.send_reset_link);
                                }
                            }
                        });
                    }
                });
                // Show the dialog
                dialog.show();
            }
        });
        phone_login_btn.setOnClickListener(v -> {
            VisibilityWithFadeEffect visibility = new VisibilityWithFadeEffect();
            if (!isPhoneLogin){
                visibility.slideUpFadeOut(ll_login_email_pass);
                visibility.slideUpFadeIn(textInputLayout_phone);
                phone_login_btn.setText(R.string.email);
                phone_login_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mail_24px, 0, 0, 0);
                isPhoneLogin = true;
            }else{
                visibility.slideDownFadeIn(ll_login_email_pass);
                visibility.slideDownFadeOut(textInputLayout_phone);
                phone_login_btn.setText(R.string.phone);
                phone_login_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone_iphone, 0, 0, 0);
                isPhoneLogin = false;

            }
        });

        google_signIn_btn.setOnClickListener(v -> {
            google_signIn_btn.setVisibility(View.GONE);
            signIn_progressBar2.setVisibility(View.VISIBLE);
            signInWithGoogle();
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visible = textInputLayout_phone.getVisibility();
                if (visible == View.VISIBLE){
                    if (!isValidFullNumber(et_login_phone.getText().toString().trim())) {
                        Toast.makeText(requireContext(), "Enter valid Phone number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    phone = "+91" + et_login_phone.getText().toString().trim();
                    sendOtp();
                    phone_login_btn.setEnabled(false);
                }else{
                    String email = et_login_email.getText().toString().trim();
                    String pass = et_login_pass.getText().toString().trim();
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        Toast.makeText(requireContext(), "Please enter valid email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (pass.isEmpty() || pass.contains(" ")){
                        Toast.makeText(requireContext(), "Pass is empty or contain blank space", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    signInWithEmailAndPass(et_login_email.getText().toString().trim(), et_login_pass.getText().toString().trim());
                }
            }
        });
    }

    private boolean isValidFullNumber(String number) {
        if (number != null) {
            return Patterns.PHONE.matcher(number).matches();
        }
        return false;
    }

    private void sendOtp() {
        login_btn.setText(R.string.logging_in_please_wait);
        AuthHelper.authenticatePhoneNumber(requireActivity(), phone, false, new AuthHelper.PhoneAuthListener() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String verificationId) {
                verificationCode = verificationId;
                Toast.makeText(requireContext(), "OTP Sent Successfully", Toast.LENGTH_SHORT).show();
                signInWithPhone();
            }
        });
    }
    private void signInWithEmailAndPass(String email, String pass){
        login_btn.setText(R.string.logging_in_please_wait);
        login_btn.setEnabled(false);
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show();
                    AuthHelper authHelper = new AuthHelper();
                    authHelper.checkUserInDatabase(requireContext(), mAuth.getUid(), new AuthHelper.OnUserCheckListener() {
                        @Override
                        public void onUserCheckComplete(String userType) {
                            login_btn.setText(R.string.text_login);
                            login_btn.setEnabled(true);
                            if (userType!=null){
                                SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(requireContext());
                                sharedPreferenceManager.setUserType(userType);
                                startActivity(new Intent(requireActivity(), MainActivity.class));
                                requireActivity().finish();
                            }else {
                                login_btn.setText(R.string.text_login);
                                login_btn.setEnabled(true);
                                Intent intent = new Intent(requireActivity(), SignUpActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                requireActivity().finish();
                            }
                        }
                    });
                }else {
                    login_btn.setText(R.string.registering_please_wait);
                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                login_btn.setText(R.string.text_login);
                                login_btn.setEnabled(true);
                                Intent intent = new Intent(requireActivity(), SignUpActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                requireActivity().finish();
                            }else {
                                login_btn.setText(R.string.text_login);
                                login_btn.setEnabled(true);
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(requireContext(), "Wrong password for this email", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(requireContext(), "Error: " + (task.getException() != null ? task.getException().getMessage() : "unknown"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void signInWithPhone() {
        login_btn.setText(R.string.text_login);
        NavDirections action = LoginFragmentDirections.actionLoginFragment2ToOtpFragment(phone, verificationCode);
        NavController navController = Navigation.findNavController(view1);
        navController.navigate(action);
        phone_login_btn.setEnabled(true);
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activityResultLauncher.launch(signInIntent);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(o.getData());
            firebaseAuthWithGoogle(task);
        }
    });

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            firebaseAuthWithGoogle(task);

        }
    }

    private void firebaseAuthWithGoogle(Task<GoogleSignInAccount> task) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                String idToken = account.getIdToken();
                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getUid();
                            AuthHelper authHelper = new AuthHelper();
                            authHelper.checkUserInDatabase(requireContext(), uid, userType -> {
                                if (userType != null) {
                                    SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(requireContext());
                                    sharedPreferenceManager.setUserType(userType);
                                    startActivity(new Intent(requireActivity(), MainActivity.class));
                                    requireActivity().finish();
                                } else {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    assert user != null;
                                    Log.d(TAG, user.getDisplayName() + user.getPhoneNumber() + user.getEmail() + user.getPhotoUrl());
                                    Bundle bundle = new Bundle();
                                    bundle.putString("name", user.getDisplayName());
                                    bundle.putString("phone", user.getPhoneNumber());
                                    bundle.putString("email", user.getEmail());
                                    bundle.putString("profileUrl", String.valueOf(user.getPhotoUrl()));
                                    Intent intent = new Intent(requireActivity(), SignUpActivity.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    requireActivity().finish();
                                }
                            });
                        } else {
                            Toast.makeText(requireContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            signIn_progressBar2.setVisibility(View.GONE);
                            google_signIn_btn.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e);
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            signIn_progressBar2.setVisibility(View.GONE);
            google_signIn_btn.setVisibility(View.VISIBLE);
        }
    }
}