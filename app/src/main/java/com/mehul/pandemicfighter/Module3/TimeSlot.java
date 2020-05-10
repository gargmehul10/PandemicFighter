package com.mehul.pandemicfighter.Module3;

public class TimeSlot {
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
}