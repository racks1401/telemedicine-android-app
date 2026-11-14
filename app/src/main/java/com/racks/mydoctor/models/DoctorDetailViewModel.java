package com.racks.mydoctor.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DoctorDetailViewModel extends ViewModel {
    private final MutableLiveData<DoctorDetailModel> doctorDetailData = new MutableLiveData<>();

    // Method to set user data
    public void setUserDetail(DoctorDetailModel userDetail) {
        doctorDetailData.setValue(userDetail);
    }

    // Method to get user data
    public LiveData<DoctorDetailModel> getUserDetail() {
        return doctorDetailData;
    }

}
