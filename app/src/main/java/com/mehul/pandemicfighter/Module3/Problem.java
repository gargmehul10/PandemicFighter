package com.mehul.pandemicfighter.Module3;

public class Problem {
    private String description, contact, UID;
    private Double lat, lng;
    private String timestamp;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Problem(String description, String contact, String UID, Double lat, Double lng, String timestamp) {
        this.description = description;
        this.contact = contact;
        this.UID = UID;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public Problem(){

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
