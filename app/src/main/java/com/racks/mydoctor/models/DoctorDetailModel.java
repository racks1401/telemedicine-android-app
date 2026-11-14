package com.racks.mydoctor.models;

import androidx.lifecycle.ViewModel;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

@Entity(tableName = "doctor_detail")
public class DoctorDetailModel extends ViewModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private final int id = 1;
    private Map<String, String> consultationModesAndCharges;
    private String name, email, phone, gender, specialization, qualification, clinicAddress, experience, aboutMe,
            profilePicUrl, doctorId, userType, medical_reg_no, working_time;
    private long dob;
    private boolean isTermsAccepted, isAvailable;
    private ArrayList<String> consultation_days, mondaySlots, tuesdaySlots, wednesdaySlots, thursdaySlots,
            fridaySlots, saturdaySlots, sundaySlots, holidays;

    public DoctorDetailModel() {
        // Initialize lists
        mondaySlots = new ArrayList<>();
        tuesdaySlots = new ArrayList<>();
        wednesdaySlots = new ArrayList<>();
        thursdaySlots = new ArrayList<>();
        fridaySlots = new ArrayList<>();
        saturdaySlots = new ArrayList<>();
        sundaySlots = new ArrayList<>();
        holidays = new ArrayList<>();
        consultation_days = new ArrayList<>();
    }

    public Map<String, String> getConsultationModesAndCharges() {
        return consultationModesAndCharges;
    }

    public void setConsultationModesAndCharges(Map<String, String> consultationModesAndCharges) {
        this.consultationModesAndCharges = consultationModesAndCharges;
    }

    public String getWorking_time() {
        return working_time;
    }

    public void setWorking_time(String working_time) {
        this.working_time = working_time;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    // Getters and Setters
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
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

    public boolean isTermsAccepted() {
        return isTermsAccepted;
    }

    public void setTermsAccepted(boolean termsAccepted) {
        isTermsAccepted = termsAccepted;
    }

    public ArrayList<String> getConsultation_days() {
        return consultation_days;
    }

    public void setConsultation_days(ArrayList<String> consultation_days) {
        this.consultation_days = consultation_days;
    }

    public ArrayList<String> getMondaySlots() {
        return mondaySlots;
    }

    public void setMondaySlots(ArrayList<String> mondaySlots) {
        this.mondaySlots = mondaySlots;
    }

    public ArrayList<String> getTuesdaySlots() {
        return tuesdaySlots;
    }

    public void setTuesdaySlots(ArrayList<String> tuesdaySlots) {
        this.tuesdaySlots = tuesdaySlots;
    }

    public ArrayList<String> getWednesdaySlots() {
        return wednesdaySlots;
    }

    public void setWednesdaySlots(ArrayList<String> wednesdaySlots) {
        this.wednesdaySlots = wednesdaySlots;
    }

    public ArrayList<String> getThursdaySlots() {
        return thursdaySlots;
    }

    public void setThursdaySlots(ArrayList<String> thursdaySlots) {
        this.thursdaySlots = thursdaySlots;
    }

    public ArrayList<String> getFridaySlots() {
        return fridaySlots;
    }

    public void setFridaySlots(ArrayList<String> fridaySlots) {
        this.fridaySlots = fridaySlots;
    }

    public ArrayList<String> getSaturdaySlots() {
        return saturdaySlots;
    }

    public void setSaturdaySlots(ArrayList<String> saturdaySlots) {
        this.saturdaySlots = saturdaySlots;
    }

    public ArrayList<String> getSundaySlots() {
        return sundaySlots;
    }

    public void setSundaySlots(ArrayList<String> sundaySlots) {
        this.sundaySlots = sundaySlots;
    }

    public ArrayList<String> getHolidays() {
        return holidays;
    }

    public void setHolidays(ArrayList<String> holidays) {
        this.holidays = holidays;
    }

    public String getMedical_reg_no() {
        return medical_reg_no;
    }

    public void setMedical_reg_no(String medical_reg_no) {
        this.medical_reg_no = medical_reg_no;
    }
}
