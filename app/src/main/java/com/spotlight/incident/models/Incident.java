package com.spotlight.incident.models;

public class Incident {

    public String incident;
    public String incident_type;
    public String description;
    public String date;
    public String location;
    public String phoneNumber;
    public String mediaUrl;
    public String userId;

    public Incident(){

    }

    public void setIncident(String incident) {
        this.incident = incident;
    }

    public void setIncident_type(String incident_type) {
        this.incident_type = incident_type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Incident(String incident, String incident_type, String description, String date , String location, String phoneNumber) {
        this.incident=incident;
        this.incident_type=incident_type;
        this.description=description;
        this.date=date;
        this.location=location;
        this.phoneNumber=phoneNumber;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getIncident() {
        return incident;
    }

    public String getIncident_type() {
        return incident_type;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public void setUserId(String userId){
        this.userId=userId;
    }
}
