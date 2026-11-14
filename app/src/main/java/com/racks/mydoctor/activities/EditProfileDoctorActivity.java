package com.racks.mydoctor.activities;

import com.racks.mydoctor.helper.InsetsHelper;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racks.mydoctor.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class EditProfileDoctorActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private final ArrayList<String> hArray = new ArrayList<>();
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;
    private TextView add_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_doctor);
        InsetsHelper.applySystemBarInsets(this);
        recyclerView = findViewById(R.id.recycler_view_holidays);
        Button update_btn = findViewById(R.id.update_btn1);
        add_btn = findViewById(R.id.add_btn);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getUid()==null) {
            Toast.makeText(this, "Auth is null", Toast.LENGTH_SHORT).show();
            return;
        }
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child("Doctors").child(mAuth.getUid());
        fetchHolidaysFromServer();

        adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            class ItemViewHolder extends RecyclerView.ViewHolder {
                final TextView tv_holiday_date;
                final ImageView iv_holiday_dlt;
                public ItemViewHolder(@NonNull View itemView) {
                    super(itemView);
                    tv_holiday_date = itemView.findViewById(R.id.tv_holiday_date);
                    iv_holiday_dlt = itemView.findViewById(R.id.iv_holiday_dlt);
                    iv_holiday_dlt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteHolidayByValue(tv_holiday_date.getText().toString());
                            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                        }
                    });
                }
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_holidays, parent, false);
                return new ItemViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                    ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                    itemViewHolder.tv_holiday_date.setText(hArray.get(position));
            }

            @Override
            public int getItemCount() {
                return hArray.size();
            }
        };
        recyclerView.setAdapter(adapter);
        update_btn.setOnClickListener(v -> {
            if (!hArray.isEmpty()){
                dbRef.child("holidays").setValue(hArray).addOnSuccessListener(unused -> Toast.makeText(EditProfileDoctorActivity.this, "Updated", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(EditProfileDoctorActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show());
            }

        });
    }

    private void fetchHolidaysFromServer() {
        dbRef.child("holidays").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hArray.clear();
                if (snapshot.exists() && snapshot.hasChildren()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        hArray.add(dataSnapshot.getValue(String.class));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select a date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        String selectedDate = sdf.format(selection);
                        if (selection<System.currentTimeMillis()){
                            Toast.makeText(EditProfileDoctorActivity.this, "Choose date after today", Toast.LENGTH_SHORT).show();
                        }else {
                            if (!hArray.contains(selectedDate)){
                                hArray.add(selectedDate);
                                adapter.notifyDataSetChanged();
                            }else {
                                Toast.makeText(EditProfileDoctorActivity.this, "Already selected", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
                materialDatePicker.show(getSupportFragmentManager(), "tag");
            }
        });
    }
    private void deleteHolidayByValue(String holidayValue) {
        dbRef.child("holidays").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String value = childSnapshot.getValue(String.class);
                    if (holidayValue.equals(value)) {
                        childSnapshot.getRef().removeValue()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        hArray.remove(holidayValue);
                                        Toast.makeText(EditProfileDoctorActivity.this, "Holiday deleted successfully!", Toast.LENGTH_SHORT).show();
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(EditProfileDoctorActivity.this, "Failed to delete holiday: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileDoctorActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}