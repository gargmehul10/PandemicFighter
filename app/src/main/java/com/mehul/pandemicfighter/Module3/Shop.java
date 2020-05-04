package com.mehul.pandemicfighter.Module3;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class Shop {

    private String shopName;
    private String ownerName;
    private double locationLatitude;
    private double locationLongitude;

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public Shop(){

    }

    public Shop(String shopName, String ownerName, double locationLatitude, double locationLongitude) {
        this.shopName = shopName;
        this.ownerName = ownerName;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
    }
}
