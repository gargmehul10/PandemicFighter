package com.mehul.pandemicfighter.Module3;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {


    private String name;
    private String MobNo;
    private String AadharNumber;
    private String Address;
    private String type;
    private Double locLatitude;
    private Double locLongitude;
    private Double range;
    private boolean isVerified;

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public User(){

    }

    public User(String name, String AadharNumber, String MobNo, String type, Double locLatitude, Double locLongitude, Double Range){
        this.name = name;
        this.MobNo = MobNo;
        this.AadharNumber = AadharNumber;
        this.Address = " ";
        this.type = type;
        this.locLatitude = locLatitude;
        this.locLongitude = locLongitude;
        this.range = Range;
        this.isVerified = false;
    }

    public Double getLocLatitude() {
        return locLatitude;
    }

    public User(Parcel in) {
        this.name = in.readString();
        this.MobNo = in.readString();
        this.AadharNumber = in.readString();
        this.Address = in.readString();
        this.type = in.readString();
        this.locLatitude = in.readDouble();
        this.locLongitude = in.readDouble();
        this.range = in.readDouble();
        this.isVerified = in.readInt() == 1;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLocLongitude() {
        return locLongitude;
    }

    public Double getRange() {
        return range;
    }

    public void setRange(Double range) {
        this.range = range;
    }

    public void setLocLatitude(Double locLatitude) {
        this.locLatitude = locLatitude;
    }

    public void setLocLongitude(Double locLongitude) {
        this.locLongitude = locLongitude;
    }

    public String getMobNo() {
        return MobNo;
    }

    public void setMobNo(String mobNo) {
        MobNo = mobNo;
    }

    public String getAadharNumber() {
        return AadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        AadharNumber = aadharNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(MobNo);
        parcel.writeString(AadharNumber);
        parcel.writeString(Address);
        parcel.writeString(type);
        parcel.writeDouble(locLatitude);
        parcel.writeDouble(locLongitude);
        parcel.writeDouble(range);
        parcel.writeInt(isVerified?1:0);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
