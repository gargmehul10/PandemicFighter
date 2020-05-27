package com.mehul.pandemicfighter.Module3;

import android.os.Parcel;
import android.os.Parcelable;

public class TimeSlot implements Parcelable {
    private int slot1, slot2, slot3, slot4;
    private String slot1UID, slot2UID, slot3UID, slot4UID;

    public TimeSlot()
    {

    }

    public TimeSlot(int slot1, int slot2, int slot3, int slot4, String slot1UID, String slot2UID, String slot3UID, String slot4UID) {
        this.slot1 = slot1;
        this.slot2 = slot2;
        this.slot3 = slot3;
        this.slot4 = slot4;
        this.slot1UID = slot1UID;
        this.slot2UID = slot2UID;
        this.slot3UID = slot3UID;
        this.slot4UID = slot4UID;
    }

    public TimeSlot(Parcel in) {
        this.slot1 = in.readInt();
        this.slot2 = in.readInt();
        this.slot3 = in.readInt();
        this.slot4 = in.readInt();
        this.slot1UID = in.readString();
        this.slot2UID = in.readString();
        this.slot3UID = in.readString();
        this.slot4UID = in.readString();
    }
    public int getSlot1() {
        return slot1;
    }

    public void setSlot1(int slot1) {
        this.slot1 = slot1;
    }

    public int getSlot2() {
        return slot2;
    }

    public void setSlot2(int slot2) {
        this.slot2 = slot2;
    }

    public int getSlot3() {
        return slot3;
    }

    public void setSlot3(int slot3) {
        this.slot3 = slot3;
    }

    public int getSlot4() {
        return slot4;
    }

    public void setSlot4(int slot4) {
        this.slot4 = slot4;
    }

    public String getSlot1UID() {
        return slot1UID;
    }

    public void setSlot1UID(String slot1UID) {
        this.slot1UID = slot1UID;
    }

    public String getSlot2UID() {
        return slot2UID;
    }

    public void setSlot2UID(String slot2UID) {
        this.slot2UID = slot2UID;
    }

    public String getSlot3UID() {
        return slot3UID;
    }

    public void setSlot3UID(String slot3UID) {
        this.slot3UID = slot3UID;
    }

    public String getSlot4UID() {
        return slot4UID;
    }

    public void setSlot4UID(String slot4UID) {
        this.slot4UID = slot4UID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(slot1);
        parcel.writeInt(slot2);
        parcel.writeInt(slot3);
        parcel.writeInt(slot4);
        parcel.writeString(slot1UID);
        parcel.writeString(slot2UID);
        parcel.writeString(slot3UID);
        parcel.writeString(slot4UID);
    }

    public static final Parcelable.Creator<TimeSlot> CREATOR = new Parcelable.Creator<TimeSlot>() {

        public TimeSlot createFromParcel(Parcel in) {
            return new TimeSlot(in);
        }

        public TimeSlot[] newArray(int size) {
            return new TimeSlot[size];
        }
    };
}