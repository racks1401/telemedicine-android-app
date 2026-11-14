package com.racks.mydoctor.models;

public class ReviewerModel {
    String name, message, profilePicUrl;

    public ReviewerModel() {
    }

    public ReviewerModel(String name, String message, String profilePicUrl) {
        this.name = name;
        this.message = message;
        this.profilePicUrl = profilePicUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}
