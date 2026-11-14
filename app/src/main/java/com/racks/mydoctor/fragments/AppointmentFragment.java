package com.racks.mydoctor.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.racks.mydoctor.R;
import com.racks.mydoctor.adapters.ViewPagerAdapter;

public class AppointmentFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);
        TabLayout tab_layout = view.findViewById(R.id.tab_layout);
        ViewPager2 view_pager = view.findViewById(R.id.view_pager);
        view_pager.setAdapter(new ViewPagerAdapter(requireActivity().getSupportFragmentManager(), getLifecycle()));
        new TabLayoutMediator(tab_layout, view_pager,
                (tab, position) -> {
                    // Set tab titles here
                    if (position == 0) {
                        tab.setText("Upcoming");
                    } else if (position == 1) {
                        tab.setText("Completed");
                    }
                }
        ).attach();
        return view;
    }
}