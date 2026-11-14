package com.racks.mydoctor.models;

import androidx.lifecycle.ViewModel;

import java.io.Serializable;
import java.util.Map;

public class AppointmentDetailModel extends ViewModel implements Serializable{

    private String patientName, patientGender, patientAge, patientWeight, patientBloodGroup, problemDesc, appointmentMode,
            appointmentTime, patientId, appointmentId, prescriptionFileUrl, appointmentCharge;
    private String doctorName, doctorSpeciality, doctorDegree, doctorId, doctorProfileUrl, doctorClinicAddress;

    long appointmentDate;
    boolean isCompleted;

    public AppointmentDetailModel() {
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientWeight() {
        return patientWeight;
    }

    public void setPatientWeight(String patientWeight) {
        this.patientWeight = patientWeight;
    }

    public String getPatientBloodGroup() {
        return patientBloodGroup;
    }

    public void setPatientBloodGroup(String patientBloodGroup) {
        this.patientBloodGroup = patientBloodGroup;
    }

    public String getProblemDesc() {
        return problemDesc;
    }

    public void setProblemDesc(String problemDesc) {
        this.problemDesc = problemDesc;
    }

    public String getAppointmentMode() {
        return appointmentMode;
    }

    public void setAppointmentMode(String appointmentMode) {
        this.appointmentMode = appointmentMode;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPrescriptionFileUrl() {
        return prescriptionFileUrl;
    }

    public void setPrescriptionFileUrl(String prescriptionFileUrl) {
        this.prescriptionFileUrl = prescriptionFileUrl;
    }

    public String getAppointmentCharge() {
        return appointmentCharge;
    }

    public void setAppointmentCharge(String appointmentCharge) {
        this.appointmentCharge = appointmentCharge;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSpeciality() {
        return doctorSpeciality;
    }

    public void setDoctorSpeciality(String doctorSpeciality) {
        this.doctorSpeciality = doctorSpeciality;
    }

    public String getDoctorDegree() {
        return doctorDegree;
    }

    public void setDoctorDegree(String doctorDegree) {
        this.doctorDegree = doctorDegree;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorProfileUrl() {
        return doctorProfileUrl;
    }

    public void setDoctorProfileUrl(String doctorProfileUrl) {
        this.doctorProfileUrl = doctorProfileUrl;
    }

    public String getDoctorClinicAddress() {
        return doctorClinicAddress;
    }

    public void setDoctorClinicAddress(String doctorClinicAddress) {
        this.doctorClinicAddress = doctorClinicAddress;
    }

    public long getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(long appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
