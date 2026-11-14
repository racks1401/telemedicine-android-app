package com.racks.mydoctor.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.racks.mydoctor.fragments.CompletedAppointmentFragment;
import com.racks.mydoctor.fragments.UpcomingAppointmentFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0){
            return new UpcomingAppointmentFragment();
        }else {
            return new CompletedAppointmentFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
