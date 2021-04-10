package com.spotlight.incident.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties

public class User {

    public String user_name;
    public String email;
    public String password;
    public String createDate;
    public String phoneNumber;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String user_name, String email, String password, String createDate, String phoneNumber) {
        this.user_name = user_name;
        this.email = email;
        this.password=password;
        this.createDate=createDate;
        this.phoneNumber=phoneNumber;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
