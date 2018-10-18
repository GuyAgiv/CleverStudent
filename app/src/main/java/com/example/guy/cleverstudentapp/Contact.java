package com.example.guy.cleverstudentapp;

import android.os.Parcelable;
import android.widget.ImageView;

public class Contact
{
    private String name;
    private String phoneNumber;
    private Boolean isAdded;
    private int profileImageResId;

    public Contact(String name, String phoneNumber, Boolean isAdded, int profileImageResId) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.isAdded = isAdded;
        this.profileImageResId = profileImageResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getAdded() {
        return isAdded;
    }

    public int getProfileImageResId() {
        return profileImageResId;
    }

    public void setProfileImageResId(int profileImageResId) {
        this.profileImageResId = profileImageResId;
    }

    public void setAdded(Boolean added) {
        isAdded = added;
    }
}
