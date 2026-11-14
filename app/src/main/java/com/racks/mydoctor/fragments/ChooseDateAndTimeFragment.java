package com.racks.mydoctor.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racks.mydoctor.R;
import com.racks.mydoctor.models.AppointmentDetailModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChooseDateAndTimeFragment extends Fragment {
    private String doctorId;
    private DatabaseReference dbRef;
    private final ArrayList<String> slotsArrayList = new ArrayList<>();
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    private AppointmentDetailModel appointmentModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_date_and_time, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_slots);
        DatePicker datePicker = view.findViewById(R.id.date_picker);
        Button next_button = view.findViewById(R.id.next_button);
        dbRef = FirebaseDatabase.getInstance().getReference("Users");
        appointmentModel = new ViewModelProvider(requireActivity()).get(AppointmentDetailModel.class);
        doctorId = appointmentModel.getDoctorId();
        int year = datePicker.getYear();
        int monthOfYear = datePicker.getMonth();
        int dayOfMonth = datePicker.getDayOfMonth();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        getSlots(calendar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    if (calendar.getTimeInMillis()<System.currentTimeMillis()) {
                        Toast.makeText(requireContext(), "Choose Date in future", Toast.LENGTH_SHORT).show();
                        slotsArrayList.clear();
                        notifyAdapter();
                    }else {
                        getSlots(calendar);
                    }

                }
            });
        }

        adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            private int selectedPosition = RecyclerView.NO_POSITION;

            class ItemViewHolder extends RecyclerView.ViewHolder {
                final TextView itemTextView;

                ItemViewHolder(View itemView) {
                    super(itemView);
                    itemTextView = itemView.findViewById(R.id.tv_slot_timing);
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int previousPosition = selectedPosition;
                            selectedPosition = getAbsoluteAdapterPosition();
                            notifyItemChanged(previousPosition);
                            notifyItemChanged(selectedPosition);

                            if (itemTextView.getText() != null) {
                                appointmentModel.setAppointmentTime((String) itemTextView.getText());
                            }
                        }
                    });
                }
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_time_slot, parent, false);
                return new ItemViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                itemViewHolder.itemTextView.setText(slotsArrayList.get(position));
                if (selectedPosition == position) {
                    itemViewHolder.itemView.setBackgroundResource(R.drawable.bg_time_slot_selected);
                } else {
                    itemViewHolder.itemView.setBackgroundResource(R.drawable.bg_time_slot_unselected);
                }
            }

            @Override
            public int getItemCount() {
                return slotsArrayList.size();
            }
        };

        recyclerView.setAdapter(adapter);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appointmentModel.getAppointmentTime()!=null){
                    NavDirections navDirections = ChooseDateAndTimeFragmentDirections.actionChooseDateAndTimeFragmentToPatientDetailFragment();
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_book_appointment);
                    navController.navigate(navDirections);
                }else {
                    Toast.makeText(requireContext(), "Please select date and time first", Toast.LENGTH_SHORT).show();
                }
                
            }
        });
        return view;
    }

    private void getSlots(Calendar calendar) {
        long selectedDateTimeStamp = calendar.getTimeInMillis();
        Date date = new Date(selectedDateTimeStamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String selectedDate = sdf.format(date);
        isHoliday(selectedDate, new FirebaseCheckCallBack() {
            @Override
            public void onSuccess(boolean isHoliday) {
                if (isHoliday) {
                    slotsArrayList.clear();
                    notifyAdapter();
                    Toast.makeText(requireContext(), "Doctor is on leave this day", Toast.LENGTH_SHORT).show();
                } else {
                    appointmentModel.setAppointmentDate(selectedDateTimeStamp);
                    String dayOfWeekString = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());
                    String str = dayOfWeekString.toLowerCase();
                    dbRef.child("Doctors").child(doctorId).child(str + "Slots").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            slotsArrayList.clear();
                            if (snapshot.exists() && snapshot.hasChildren()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    slotsArrayList.add(dataSnapshot.getValue(String.class));
                                }
                            }
                            notifyAdapter();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onError(String errorMsg) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void isHoliday(String selectedDate, FirebaseCheckCallBack firebaseCheckCallBack) {
        dbRef.child("Doctors").child(doctorId).child("holidays").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<String> holidays = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String tempDate = dataSnapshot.getValue(String.class);
                        holidays.add(tempDate);
                    }
                    firebaseCheckCallBack.onSuccess(holidays.contains(selectedDate));
                } else {
                    firebaseCheckCallBack.onSuccess(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                firebaseCheckCallBack.onError("Error: "+error.getMessage());
            }
        });
    }

    private interface FirebaseCheckCallBack {
        void onSuccess(boolean isHoliday);

        void onError(String errorMsg);
    }
    public void notifyAdapter(){
        adapter.notifyDataSetChanged();
    }
}
