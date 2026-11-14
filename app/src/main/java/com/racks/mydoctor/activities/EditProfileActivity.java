package com.racks.mydoctor.activities;

import java.util.HashMap;
import java.util.Map;
import com.racks.mydoctor.helper.SharedPreferenceManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racks.mydoctor.R;
import com.racks.mydoctor.helper.ImageUploadHelper;
import com.racks.mydoctor.models.UserDetailModel;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputLayout et_fullName, et_email, et_phone, et_dob, et_address;
    private ImageView iv_profile_pic, iv_update_pic;
    private MaterialButton btn_next;
    private RadioGroup genderRadioGroup;
    private ProgressBar progressBar_upload_profile_pic;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private UserDetailModel userProfileData;
    private final int SELECT_PICTURE = 1;
    private String selectedGender = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        et_fullName = findViewById(R.id.et_fullName);
        et_email = findViewById(R.id.et_email);
        et_phone = findViewById(R.id.et_phone);
        et_dob = findViewById(R.id.et_dob);
        genderRadioGroup = findViewById(R.id.gender_radio_group);
        et_address = findViewById(R.id.et_address);
        btn_next = findViewById(R.id.btn_update);
        iv_profile_pic = findViewById(R.id.iv_profile_pic);
        iv_update_pic = findViewById(R.id.iv_update_pic);
        progressBar_upload_profile_pic = findViewById(R.id.progressBar_upload_profile_pic);
        userProfileData = new UserDetailModel();
        iv_update_pic.setOnClickListener(v-> openGallery());
        et_dob.getEditText().setOnClickListener(v -> showDatePickerDialog((selectedDate, selectedTimeInMillis) -> {
            et_dob.getEditText().setText(selectedDate);
            userProfileData.setDob(selectedTimeInMillis);
        }));
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                selectedGender = radioButton.getText().toString();
            }
        });
        btn_next.setOnClickListener(v -> updateUserProfile());
    }

    private void getAndSetData() {
        if (mAuth.getUid() == null) {
            showMessage("Please login again");
            return;
        }
        getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()){
                    et_fullName.getEditText().setText(snapshot.child("name").getValue(String.class));
                    et_phone.getEditText().setText(snapshot.child("phone").getValue(String.class));
                    et_email.getEditText().setText(snapshot.child("email").getValue(String.class));
                    et_address.getEditText().setText(snapshot.child(getAddressKey()).getValue(String.class));
                    Long dobTimestamp = snapshot.child("dob").getValue(Long.class);
                    if (dobTimestamp != null && dobTimestamp != 0) {
                        userProfileData.setDob(dobTimestamp);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        et_dob.getEditText().setText(dateFormat.format(new Date(dobTimestamp)));
                    }
                    String gender = snapshot.child("gender").getValue(String.class);
                    selectGenderRadioButton(gender);
                    selectedGender = gender;
                    String profileUrl = snapshot.child("profilePicUrl").getValue(String.class);
                    setProfilePic(profileUrl);
                }else {
                    showMessage("No data available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage(error.getMessage());
            }
        });


    }
    private DatabaseReference getUserRef() {
        String userType = new SharedPreferenceManager(this).getUserType();
        if (userType == null) userType = "Patient";
        return dbRef.child("Users").child(userType + "s").child(mAuth.getUid());
    }

    private String getAddressKey() {
        return "Doctor".equals(new SharedPreferenceManager(this).getUserType()) ? "clinicAddress" : "address";
    }

    private void selectGenderRadioButton(String gender) {
        for (int i = 0; i < genderRadioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) genderRadioGroup.getChildAt(i);
            if (radioButton.getText().toString().equals(gender)) {
                radioButton.setChecked(true);
                break;
            }
        }
    }
    private void updateUserProfile() {
        TextInputLayout[] textInputLayouts = new TextInputLayout[]{et_fullName, et_email, et_phone, et_dob, et_address};
        for (TextInputLayout editTexts : textInputLayouts) {
            if ((editTexts.getEditText().getText().toString().trim().isEmpty())) {
                showMessage("All fields are necessary");
                return;
            }
        }
        if (selectedGender==null){
            showMessage("Select gender first");
            return;
        }
        String userName = et_fullName.getEditText().getText().toString().trim();
        String email = et_email.getEditText().getText().toString().trim();
        String phone = et_phone.getEditText().getText().toString().trim();
        String address = et_address.getEditText().getText().toString().trim();
        String profileUrl = userProfileData.getProfilePicUrl();
        long dob = userProfileData.getDob();
        if (mAuth.getUid() == null) {
            showMessage("Please login again");
            return;
        }
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", userName);
        updates.put("email", email);
        updates.put("phone", phone);
        updates.put(getAddressKey(), address);
        updates.put("profilePicUrl", profileUrl);
        updates.put("gender", selectedGender);
        updates.put("dob", dob);
        // updateChildren keeps other fields (userType, slots, termsAccepted...) intact
        getUserRef().updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showMessage("Profile Updated");
            } else {
                showMessage(Objects.requireNonNull(task.getException()).toString());
            }
        });

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityIfNeeded(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                compressAndUploadImage(selectedImageUri, bitmap);
            } catch (Exception e) {
                Log.e("Exception", "EditProfile onActivityResult:- "+e);
            }
        } else {
            showMessage("please select an Image");
        }
    }

    private void compressAndUploadImage(Uri selectedImageUri, Bitmap bitmap) {
        ImageUploadHelper.compressImage(this, selectedImageUri, bitmap, new ImageUploadHelper.UploadListener() {
            @Override
            public void onSuccess(String downloadableUrl) {
                progressBar_upload_profile_pic.setVisibility(View.GONE);
                setProfilePic(downloadableUrl);
            }

            @Override
            public void onProgressChanged(double progress) {
                progressBar_upload_profile_pic.setVisibility(View.VISIBLE);
                progressBar_upload_profile_pic.setProgress((int) progress);
            }

            @Override
            public void onFailure(String message) {
                progressBar_upload_profile_pic.setVisibility(View.GONE);
                showMessage(message);
            }
        });
    }

    private void setProfilePic(String profileUrl) {
        userProfileData.setProfilePicUrl(profileUrl);
        try {
            Glide.with(this)
                    .load(profileUrl)
                    .placeholder(R.drawable.profile_placeholder)
                    .into(iv_profile_pic);
        }catch (IllegalArgumentException illegalArgumentException){
            illegalArgumentException.printStackTrace();
        }
    }

    private void showDatePickerDialog(DateSelectionCallback dateSelectionCallback) {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDateCalendar = Calendar.getInstance();
                    selectedDateCalendar.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String selectedDate = dateFormat.format(selectedDateCalendar.getTime());
                    long selectedTimeInMillis = selectedDateCalendar.getTimeInMillis();
                    dateSelectionCallback.onDateSelected(selectedDate, selectedTimeInMillis);
                }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // Optional: Set maximum date to today
        datePickerDialog.show();
    }

    public interface DateSelectionCallback {
        void onDateSelected(String selectedDate, long selectedTimeInMillis);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAndSetData();
    }
}