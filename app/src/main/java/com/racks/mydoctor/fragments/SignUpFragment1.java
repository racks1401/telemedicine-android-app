package com.racks.mydoctor.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.racks.mydoctor.R;
import com.racks.mydoctor.activities.MainActivity;
import com.racks.mydoctor.activities.SignUpActivity;
import com.racks.mydoctor.helper.ImageUploadHelper;
import com.racks.mydoctor.helper.SharedPreferenceManager;
import com.racks.mydoctor.models.DoctorDetailModel;
import com.racks.mydoctor.models.UserDetailModel;
import com.racks.mydoctor.models.UserDetailModel2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class SignUpFragment1 extends Fragment {

    private TextInputLayout et_fullName, et_email, et_phone, et_dob, et_address;
    private ImageView iv_profile_pic, iv_update_pic;
    private MaterialButton btn_next;
    private ProgressBar progressBar_upload_profile_pic;
    private RadioGroup userTypeRadioGroup;
    private RadioGroup genderRadioGroup;
    private String selectedUserType;
    private String selectedGender;
    private long selectedDob;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private UserDetailModel2 userDetailModel2;
    private DoctorDetailModel doctorDetailModel;
    private String profilePicUrl;
    private Dialog progressDialog;
    private final int SELECT_PICTURE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up1, container, false);
        et_fullName = view.findViewById(R.id.et_fullName);
        et_email = view.findViewById(R.id.et_email);
        et_phone = view.findViewById(R.id.et_phone);
        et_dob = view.findViewById(R.id.et_dob);
        et_address = view.findViewById(R.id.et_address);
        btn_next = view.findViewById(R.id.btn_next1);
        userTypeRadioGroup = view.findViewById(R.id.user_type_radio_group);
        genderRadioGroup = view.findViewById(R.id.select_gender_radio_group);
        iv_profile_pic = view.findViewById(R.id.iv_profile_pic);
        iv_update_pic = view.findViewById(R.id.iv_update_pic);
        progressBar_upload_profile_pic = view.findViewById(R.id.progressBar_upload_profile_pic);

        et_dob.getEditText().setOnClickListener(v -> showDatePickerDialog(new DateSelectionCallback() {
            @Override
            public void onDateSelected(String selectedDate, long selectedTimeInMillis) {
                et_dob.getEditText().setText(selectedDate);
                selectedDob = selectedTimeInMillis;
            }
        }));

        btn_next.setOnClickListener(v -> createUserProfile());
        iv_update_pic.setOnClickListener(v -> openGallery());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        getAndSetData(bundle);
    }

    private void getAndSetData(Bundle bundle) {
        if (bundle != null) {
            if (bundle.containsKey("profileUrl")) {
                setProfilePic(bundle.getString("profileUrl"));
            }
            if (bundle.containsKey("name")) {
                et_fullName.getEditText().setText(bundle.getString("name"));
            }
            if (bundle.containsKey("phone")) {
                et_phone.getEditText().setText(bundle.getString("phone"));
            }
            if (bundle.containsKey("email")) {
                et_email.getEditText().setText(bundle.getString("email"));
            }
        }

        userTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rButton = group.findViewById(checkedId);
                selectedUserType = rButton.getText().toString().trim();
            }
        });
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rButton = group.findViewById(checkedId);
                selectedGender = rButton.getText().toString().trim();
            }
        });

    }

    private void createUserProfile() {
        TextInputLayout[] textInputLayouts = new TextInputLayout[]{et_fullName, et_email, et_phone, et_dob};
        for (TextInputLayout editTexts : textInputLayouts) {
            if ((editTexts.getEditText().getText().toString().trim().isEmpty())) {
                showToast("All fields are necessary");
                return;
            }
        }
        if (selectedUserType == null) {
            showToast("Please Select Your User Type");
            return;
        }
        if (selectedGender==null){
            showToast("Please Select Your Gender");
            return;
        }
        if (selectedUserType.equals("Doctor")) {
            doctorDetailModel = new ViewModelProvider(requireActivity()).get(DoctorDetailModel.class);
            doctorDetailModel.setName(et_fullName.getEditText().getText().toString().trim());
            doctorDetailModel.setEmail(et_email.getEditText().getText().toString().trim());
            doctorDetailModel.setPhone(et_phone.getEditText().getText().toString().trim());
            doctorDetailModel.setClinicAddress(et_address.getEditText().getText().toString().trim());
            doctorDetailModel.setGender(selectedGender);
            doctorDetailModel.setUserType(selectedUserType);
            doctorDetailModel.setProfilePicUrl(profilePicUrl);
            doctorDetailModel.setDob(selectedDob);

            NavDirections action = SignUpFragment1Directions.actionSignUpFragment1ToSignUpFragment2();
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_sign_up);
            navController.navigate(action);
        } else {
            userDetailModel2 = new ViewModelProvider(requireActivity()).get(UserDetailModel2.class);
            userDetailModel2.setName(et_fullName.getEditText().getText().toString().trim());
            userDetailModel2.setEmail(et_email.getEditText().getText().toString().trim());
            userDetailModel2.setPhone(et_phone.getEditText().getText().toString().trim());
            userDetailModel2.setAddress(et_address.getEditText().getText().toString().trim());
            userDetailModel2.setGender(selectedGender);
            userDetailModel2.setUserType(selectedUserType);
            userDetailModel2.setProfilePicUrl(profilePicUrl);
            userDetailModel2.setDob(selectedDob);

            NavDirections action = SignUpFragment1Directions.actionSignUpFragment1ToSignUpFragment4();
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_sign_up);
            navController.navigate(action);
        }

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                compressAndUploadImage(selectedImageUri, bitmap);
            } catch (Exception e) {
                Log.e("Exception", "SignUp onActivityResult:- " + e);
            }
        } else {
            showToast("please select an Image");
        }
    }

    private void compressAndUploadImage(Uri selectedImageUri, Bitmap bitmap) {
        ImageUploadHelper.compressImage(requireContext(), selectedImageUri, bitmap, new ImageUploadHelper.UploadListener() {
            @Override
            public void onSuccess(String downloadableUrl) {
                progressBar_upload_profile_pic.setVisibility(View.GONE);
                profilePicUrl = downloadableUrl;
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
                showToast(message);
            }
        });
    }

    private void setProfilePic(String profilePicUrl1) {
        Glide.with(requireContext())
                .load(Uri.parse(profilePicUrl1))
                .placeholder(R.drawable.profile_placeholder)
                .into(iv_profile_pic);
        profilePicUrl = profilePicUrl1;
    }

    private void showDatePickerDialog(DateSelectionCallback dateSelectionCallback) {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(),
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

    private interface DateSelectionCallback {
        void onDateSelected(String selectedDate, long selectedTimeInMillis);
    }

    private void showToast(String msg) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((SignUpActivity) requireActivity()).updateLinearProgress(1);
    }
}