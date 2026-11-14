package com.racks.mydoctor.models;

import androidx.lifecycle.ViewModel;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_detail")
public class UserDetailModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name, email, phone, address, profilePicUrl, gender, userType;
    private long dob;
    private boolean isAvailable,  isTermsAccepted;

    public UserDetailModel() {
    }

    public UserDetailModel(String name, String email, String phone, String address, String profilePicUrl, String gender, String userType, long dob, boolean isAvailable) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.profilePicUrl = profilePicUrl;
        this.gender = gender;
        this.userType = userType;
        this.dob = dob;
        this.isAvailable = isAvailable;
    }

    @Ignore
    public UserDetailModel(String name, String email, String phone, String address, String profilePicUrl, String gender, long dob) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.profilePicUrl = profilePicUrl;
        this.gender = gender;
        this.dob = dob;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public long getDob() {
        return dob;
    }

    public void setDob(long dob) {
        this.dob = dob;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isTermsAccepted() {
        return isTermsAccepted;
    }

    public void setTermsAccepted(boolean termsAccepted) {
        isTermsAccepted = termsAccepted;
    }
}
