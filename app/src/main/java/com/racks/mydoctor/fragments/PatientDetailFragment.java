package com.racks.mydoctor.fragments;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
import static android.app.Activity.RESULT_OK;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.racks.mydoctor.R;
import com.racks.mydoctor.models.AppointmentDetailModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class PatientDetailFragment extends Fragment {

    private EditText et_patient_name, et_patient_age, et_patient_weight,
            et_patient_symptoms;
    private TextView tv_selected_file_name;
    private AutoCompleteTextView patient_gender, patient_blood_group;
    private ImageView img_select_file, iv_back_btn;
    private Button upload_file_button, next_button;
    private LinearLayout ll_uploaded_msg;
    private AppointmentDetailModel appointmentDetailModel;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int IMAGE_PICK_CODE = 101;
    private final ArrayList<String> tempUploadedFileUrl = new ArrayList<>();
    private Uri selectedImageUri;
    private String selectedFileName;
    private NavDirections navDirections;
    private NavController navController;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_detail, container, false);
        et_patient_name = view.findViewById(R.id.et_patient_name);
        patient_gender = view.findViewById(R.id.patient_gender);
        et_patient_age = view.findViewById(R.id.et_patient_age);
        et_patient_weight = view.findViewById(R.id.et_patient_weight);
        patient_blood_group = view.findViewById(R.id.patient_blood_group);
        tv_selected_file_name = view.findViewById(R.id.tv_selected_file_name);
        et_patient_symptoms = view.findViewById(R.id.et_patient_symptoms);
        img_select_file = view.findViewById(R.id.img_select_file);
        iv_back_btn = view.findViewById(R.id.iv_back_btn);
        upload_file_button = view.findViewById(R.id.upload_file_button);
        next_button = view.findViewById(R.id.next_button);
        ll_uploaded_msg = view.findViewById(R.id.ll_uploaded_msg);
        appointmentDetailModel = new ViewModelProvider(requireActivity()).get(AppointmentDetailModel.class);
        navDirections = PatientDetailFragmentDirections.actionPatientDetailFragmentToModeSelectionFragment();
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_book_appointment);

        initializeSpinners();
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        String fileExtension = getFileExtension(selectedImageUri);
                        selectedFileName = generateFileName(fileExtension);
                        tv_selected_file_name.setText(selectedFileName);
                    }
                }
        );

        img_select_file.setOnClickListener(v -> {
            if (checkPermission()) {
                pickImage();
            } else {
                requestPermission();
            }
        });
        upload_file_button.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                upload_file_button.setEnabled(false);
                upload_file_button.setText(R.string.uploading_please_wait);
                uploadFile();
            }else{
                Toast.makeText(requireContext(), "Please choose a file first", Toast.LENGTH_SHORT).show();
            }
        });
        iv_back_btn.setOnClickListener(v -> navController.popBackStack());
        next_button.setOnClickListener(v -> goNext());
        return view;
    }

    private void initializeSpinners() {
        String[] itemsGender = getResources().getStringArray(R.array.dropdown_items_gender);
        String[] itemsBloodGroup = getResources().getStringArray(R.array.dropdown_items_blood_group);

        ArrayAdapter<String> adapterGender = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, itemsGender);
        ArrayAdapter<String> adapterBloodGroup = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, itemsBloodGroup);
        patient_gender.setAdapter(adapterGender);
        patient_blood_group.setAdapter(adapterBloodGroup);
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void goNext() {
        String name = et_patient_name.getText().toString().trim();
        String gender = patient_gender.getText().toString().trim();
        String age = et_patient_age.getText().toString().trim();
        String weight = et_patient_weight.getText().toString().trim();
        String bloodGroup = patient_blood_group.getText().toString().trim();
        String problemDesc = et_patient_symptoms.getText().toString().trim();

        if (!isValidName(name)) {
            et_patient_name.setError("Please Enter Valid Name");
            et_patient_name.requestFocus();
            return;
        }
        if (gender.isEmpty()) {
            patient_gender.setError("Please Select Gender first");
            patient_gender.requestFocus();
            return;

        }
        if (age.isEmpty()) {
            et_patient_age.setError("Age is mandatory");
            et_patient_age.requestFocus();
            return;
        }
        if (weight.isEmpty()) {
            weight = "unknown";
        }
        if (bloodGroup.isEmpty()) {
            patient_blood_group.setError("Please Select Blood Group");
            patient_blood_group.requestFocus();
            return;
        }
        appointmentDetailModel.setPatientName(name);
        appointmentDetailModel.setPatientGender(gender);
        appointmentDetailModel.setPatientAge(age + " years");
        appointmentDetailModel.setPatientWeight(weight);
        appointmentDetailModel.setPatientBloodGroup(bloodGroup);
        appointmentDetailModel.setProblemDesc(problemDesc);
        navController.navigate(navDirections);
    }

    private void uploadFile() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("uploads/images/" + selectedFileName);

        // Upload the file to Firebase Storage
        UploadTask uploadTask = fileRef.putFile(selectedImageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    appointmentDetailModel.setPrescriptionFileUrl(uri.toString());
                    Toast.makeText(requireContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    selectedImageUri = null;
                    tv_selected_file_name.setText("");
                    upload_file_button.setEnabled(true);
                    upload_file_button.setText(R.string.upload);
                    showUploadMessage(uri.toString(), true);
                }
            });
        }).addOnFailureListener(e -> {
            // Image upload failed
            Toast.makeText(requireContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            selectedImageUri = null;
            tv_selected_file_name.setText("");
            upload_file_button.setEnabled(true);
            upload_file_button.setText(R.string.upload);
            showUploadMessage("", false);
        });


    }

    private boolean isValidName(String name) {
        String regex = "^[A-Za-z\\s]+$";
        return name.matches(regex);
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return ContextCompat.checkSelfPermission(requireActivity(), READ_MEDIA_VISUAL_USER_SELECTED) == PERMISSION_GRANTED;

        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(requireActivity(), READ_MEDIA_IMAGES) == PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(requireActivity(), READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED}, PERMISSION_REQUEST_CODE);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO}, PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            Map<String, Integer> perms = new HashMap<>();
            for (int i = 0; i < permissions.length; i++) {
                perms.put(permissions[i], grantResults[i]);
            }

            boolean allGranted = true;
            for (String permission : permissions) {
                if (perms.get(permission) != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                pickImage();
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = requireActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private String generateFileName(String fileExtension) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        return "IMG_" + timeStamp + "." + fileExtension;
    }

    private void showUploadMessage(String filUri, boolean uploaded) {
        TextView tv_msg = new TextView(requireContext());
        tv_msg.setText(selectedFileName);
        Drawable doneDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_done);
        tv_msg.setCompoundDrawables(null, null, doneDrawable, null);
        if (uploaded) {
            tv_msg.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));
            tempUploadedFileUrl.add(filUri);
        } else {
            tv_msg.setTextColor(ContextCompat.getColor(requireContext(), R.color.textColor));
        }
        ll_uploaded_msg.addView(tv_msg);
    }
}