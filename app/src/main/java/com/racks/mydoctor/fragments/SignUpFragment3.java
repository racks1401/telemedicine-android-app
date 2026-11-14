package com.racks.mydoctor.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.racks.mydoctor.R;
import com.racks.mydoctor.activities.SignUpActivity;
import com.racks.mydoctor.models.DoctorDetailModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SignUpFragment3 extends Fragment {
    private CheckBox checkbox_clinic_visit, checkbox_messaging, checkbox_voice, checkbox_video;
    private EditText et_clinic_address;
    private TextInputLayout et_start_time, et_end_time, et_clinic_visit_charge, et_messaging_charge,
            et_voice_charge,et_video_charge ;
    private MaterialButton btn_next3;
    private GridLayout gridLayout_days;
    private DoctorDetailModel doctorDetailModel;
    private final ArrayList<String> availableDays = new ArrayList<>();
    private final Set<TextView> selectedTextViews = new HashSet<>();
    private final ArrayList<String> tempMode = new ArrayList<>();
    private final ArrayList<String> tempCharge = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up3, container, false);
        checkbox_clinic_visit = view.findViewById(R.id.checkbox_clinic_visit);
        checkbox_messaging = view.findViewById(R.id.checkbox_messaging);
        checkbox_voice = view.findViewById(R.id.checkbox_voice);
        checkbox_video = view.findViewById(R.id.checkbox_video);
        gridLayout_days = view.findViewById(R.id.gridLayout_days);
        et_clinic_address = view.findViewById(R.id.et_clinic_address);
        et_start_time = view.findViewById(R.id.et_start_time);
        et_end_time = view.findViewById(R.id.et_end_time);
        et_clinic_visit_charge = view.findViewById(R.id.et_clinic_visit_charge);
        et_messaging_charge = view.findViewById(R.id.et_messaging_charge);
        et_voice_charge = view.findViewById(R.id.et_voice_charge);
        et_video_charge = view.findViewById(R.id.et_video_charge);
        btn_next3 = view.findViewById(R.id.btn_next3);
        doctorDetailModel = new ViewModelProvider(requireActivity()).get(DoctorDetailModel.class);
        setLongClickListener();

        return view;
    }

    private void setLongClickListener() {

        for (int i = 0; i < gridLayout_days.getChildCount(); i++) {
            View child = gridLayout_days.getChildAt(i);
            if (child instanceof TextView) {
                child.setOnClickListener(v -> {
                    TextView textView = (TextView) v;
                    if (selectedTextViews.contains(textView)) {
                        textView.setSelected(false);
                        selectedTextViews.remove(textView);
                        availableDays.remove(textView.getText().toString());
                        Log.d("signUpFragment3", "arraylist-  " + availableDays);
                    } else {
                        textView.setSelected(true);
                        selectedTextViews.add(textView);
                        availableDays.add(textView.getText().toString());
                        Log.d("signUpFragment3", "arraylist-  " + availableDays);
                    }
                });
                child.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        TextView textView = (TextView) v;
                        String dayName = textView.getText().toString();
                        switch (dayName) {
                            case "Monday":
                                showDialog(doctorDetailModel.getMondaySlots());
                                break;
                            case "Tuesday":
                                showDialog(doctorDetailModel.getTuesdaySlots());
                                break;
                            case "Wednesday":
                                showDialog(doctorDetailModel.getWednesdaySlots());
                                break;
                            case "Thursday":
                                showDialog(doctorDetailModel.getThursdaySlots());
                                break;
                            case "Friday":
                                showDialog(doctorDetailModel.getFridaySlots());
                                break;
                            case "Saturday":
                                showDialog(doctorDetailModel.getSaturdaySlots());
                                break;
                            default:
                                showDialog(doctorDetailModel.getSundaySlots());
                                break;
                        }
                        return true;
                    }
                });
            }
        }

        et_start_time.getEditText().setOnClickListener(v -> showTimePickerDialog(formattedTime -> et_start_time.getEditText().setText(formattedTime)));
        et_end_time.getEditText().setOnClickListener(v -> showTimePickerDialog(formattedTime -> et_end_time.getEditText().setText(formattedTime)));

        btn_next3.setOnClickListener(v -> {
            tempMode.clear();
            tempCharge.clear();
            if (!checkbox_clinic_visit.isChecked() && !checkbox_messaging.isChecked() && !checkbox_voice.isChecked() && !checkbox_video.isChecked()) {
                Toast.makeText(requireContext(), "You must select atLeast one appointment mode.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (checkbox_clinic_visit.isChecked()) {
                if (et_clinic_visit_charge.getEditText().getText().toString().isEmpty()) {
                    et_clinic_visit_charge.setError("required");
                    et_clinic_visit_charge.requestFocus();
                    return;
                } else {
                    tempMode.add("Clinic Visit");
                    String strCharge =et_clinic_visit_charge.getEditText().getText().toString()+"rs";
                    tempCharge.add(strCharge);
                }
                if (et_clinic_address.getText().toString().isEmpty()){
                    et_clinic_address.setError("Field is required if you are giving clinic visit facility");
                    et_clinic_address.requestFocus();
                    return;
                }
                doctorDetailModel.setClinicAddress(et_clinic_address.getText().toString().trim());
            }
            if (checkbox_messaging.isChecked()) {
                if (et_messaging_charge.getEditText().getText().toString().isEmpty()) {
                    et_messaging_charge.setError("required");
                    et_messaging_charge.requestFocus();
                    return;
                } else {
                    tempMode.add("Messaging");
                    String strCharge = et_messaging_charge.getEditText().getText().toString()+"rs";
                    tempCharge.add(strCharge);
                }
            }
            if (checkbox_voice.isChecked()) {
                if (et_voice_charge.getEditText().getText().toString().isEmpty()) {
                    et_voice_charge.setError("required");
                    et_voice_charge.requestFocus();
                    return;
                } else {
                    tempMode.add("Voice Call");
                    String strCharge = et_voice_charge.getEditText().getText().toString()+"rs";
                    tempCharge.add(strCharge);
                }
            }
            if (checkbox_video.isChecked()) {
                if (et_video_charge.getEditText().getText().toString().isEmpty()) {
                    et_video_charge.setError("required");
                    et_video_charge.requestFocus();
                    return;
                } else {
                    tempMode.add("Video Call");
                    String strCharge = et_video_charge.getEditText().getText().toString()+"rs";
                    tempCharge.add(strCharge);
                }
            }

            String startTime = et_start_time.getEditText().getText().toString();
            String endTime = et_end_time.getEditText().getText().toString();
            if (startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your working hour", Toast.LENGTH_SHORT).show();
                return;
            }
            if (availableDays.isEmpty()) {
                Toast.makeText(requireContext(), "You must add atLeast one day.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (doctorDetailModel.getMondaySlots().isEmpty() && doctorDetailModel.getTuesdaySlots().isEmpty() && doctorDetailModel.getWednesdaySlots().isEmpty()
                    && doctorDetailModel.getThursdaySlots().isEmpty() && doctorDetailModel.getFridaySlots().isEmpty() && doctorDetailModel.getSaturdaySlots().isEmpty() && doctorDetailModel.getSundaySlots().isEmpty()) {
                Toast.makeText(requireContext(), "You must add time slots for atLeast one day.", Toast.LENGTH_SHORT).show();
                return;
            }
            doctorDetailModel.setConsultation_days(availableDays);
            setConsultationModesAndCharges();
            String strWorkingTime = "Working Hours | "+ startTime+" - "+endTime;
            doctorDetailModel.setWorking_time(strWorkingTime);
            NavDirections action = SignUpFragment3Directions.actionSignUpFragment3ToSignUpFragment4();
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_sign_up);
            navController.navigate(action);

        });
    }

    private void showTimePickerDialog(final OnTimeSelectedListener listener) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar selectedTime = Calendar.getInstance();
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);

                        String formattedTime = DateFormat.format("hh:mm a", selectedTime).toString();
                        listener.onTimeSelected(formattedTime);
                    }
                }, hour, minute, false);

        timePickerDialog.show();
    }

    public interface OnTimeSelectedListener {
        void onTimeSelected(String formattedTime);
    }

    // Method to show dialog
    private void showDialog(ArrayList<String> timeSlots) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_time_slot, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        GridLayout dialogGridLayout = dialogView.findViewById(R.id.dialog_grid_layout);
        TextView tvCancel = dialogView.findViewById(R.id.tv_cancel);
        TextView tvDone = dialogView.findViewById(R.id.tv_done);
        CheckBox addToAll = dialogView.findViewById(R.id.add_slots_to_all);

        addViewsFromArrayList(dialogGridLayout, timeSlots);

        tvCancel.setOnClickListener(v -> dialog.dismiss());
        tvDone.setOnClickListener(v -> {
            if (addToAll.isChecked()) {
                ArrayList<String> tempArray = new ArrayList<>(timeSlots);
                Log.d("signUpFragment3 ", "checked");
                addSlotsToAll(tempArray);
            }
            dialog.dismiss();
        });

    }

    private void addSlotsToAll(ArrayList<String> timeSlots) {
        doctorDetailModel.getMondaySlots().clear();
        doctorDetailModel.setMondaySlots(new ArrayList<>(timeSlots));
        doctorDetailModel.getTuesdaySlots().clear();
        doctorDetailModel.setTuesdaySlots(new ArrayList<>(timeSlots));
        doctorDetailModel.getWednesdaySlots().clear();
        doctorDetailModel.setWednesdaySlots(new ArrayList<>(timeSlots));
        doctorDetailModel.getThursdaySlots().clear();
        doctorDetailModel.setThursdaySlots(new ArrayList<>(timeSlots));
        doctorDetailModel.getFridaySlots().clear();
        doctorDetailModel.setFridaySlots(new ArrayList<>(timeSlots));
        doctorDetailModel.getSaturdaySlots().clear();
        doctorDetailModel.setSaturdaySlots(new ArrayList<>(timeSlots));
        doctorDetailModel.getSundaySlots().clear();
        doctorDetailModel.setSundaySlots(new ArrayList<>(timeSlots));
    }

    // Method to add a custom list item to the GridLayout inside the dialog
    private void addCustomListItem(GridLayout gridLayout, String
            selectedTime, ArrayList<String> timeSlot1) {
        LayoutInflater inflater = getLayoutInflater();
        View customItemView = inflater.inflate(R.layout.list_item_time_slot, gridLayout, false);

        // Optionally, modify the custom item view (e.g., set the text if needed)
        TextView customItemTextView = customItemView.findViewById(R.id.tv_slot_timing);
        customItemTextView.setText(selectedTime);
        // Add the custom item view to the GridLayout at the start position
        if (!timeSlot1.contains(selectedTime)) {
            gridLayout.addView(customItemView, 0);
            timeSlot1.add(selectedTime);
        }
        customItemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                // Remove the view from the GridLayout
                gridLayout.removeView(v);

                // Remove the corresponding item from the timeSlots list
                timeSlot1.remove(selectedTime);

                return true; // Return true to indicate the event was handled
            }
        });
    }

    private void addViewsFromArrayList(GridLayout gridLayout, ArrayList<String> timeSlots) {
        // Clear the GridLayout before adding new views (if necessary)
//        gridLayout.removeAllViews();
        // Iterate over the ArrayList
        for (String timeSlot : timeSlots) {
            // Inflate the custom item view
            LayoutInflater inflater = getLayoutInflater();
            View customItemView = inflater.inflate(R.layout.list_item_time_slot, gridLayout, false);

            // Set the text for the custom item view
            TextView customItemTextView = customItemView.findViewById(R.id.tv_slot_timing);
            customItemTextView.setText(timeSlot);

            // Add the custom item view to the GridLayout
            gridLayout.addView(customItemView);

            // Optionally, set an OnClickListener or OnLongClickListener for the view
            customItemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Remove the view from the GridLayout
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    gridLayout.removeView(v);

                    // Remove the corresponding item from the timeSlots list
                    timeSlots.remove(timeSlot);

                    return true; // Return true to indicate the event was handled
                }
            });
        }
        ImageView imageView = new ImageView(requireContext());
        imageView.setImageResource(R.drawable.add_circle_32);

        int paddingInDp = (int) (4 * getResources().getDisplayMetrics().density + 0.5f);
        imageView.setPadding(paddingInDp, paddingInDp, paddingInDp, paddingInDp);
        gridLayout.addView(imageView);
        imageView.setOnClickListener(v -> showTimePickerDialog(new OnTimeSelectedListener() {
            @Override
            public void onTimeSelected(String formattedTime) {
                addCustomListItem(gridLayout, formattedTime, timeSlots);
            }
        }));
    }

    private void setConsultationModesAndCharges() {
        Map<String, String> consultationModesAndCharges = new HashMap<>();
        if (!tempMode.isEmpty()) {
            for (int i = 0; i < tempMode.size(); i++) {
                consultationModesAndCharges.put(tempMode.get(i), tempCharge.get(i));
            }
        }
        doctorDetailModel.setConsultationModesAndCharges(consultationModesAndCharges);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((SignUpActivity) requireActivity()).updateLinearProgress(3);
    }
}