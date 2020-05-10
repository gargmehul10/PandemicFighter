package com.mehul.pandemicfighter.Module3;

public class Transaction {
    private String timestamp;
    private int rice, pulses, flour, cookingOil, spices;

    public Transaction()
    {

    }

    public Transaction(String timestamp, int rice, int pulses, int flour, int cookingOil, int spices) {
        this.timestamp = timestamp;
        this.rice = rice;
        this.pulses = pulses;
        this.flour = flour;
        this.cookingOil = cookingOil;
        this.spices = spices;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getRice() {
        return rice;
    }

    public void setRice(int rice) {
        this.rice = rice;
    }

    public int getPulses() {
        return pulses;
    }

    public void setPulses(int pulses) {
        this.pulses = pulses;
    }

    public int getFlour() {
        return flour;
    }

    public void setFlour(int flour) {
        this.flour = flour;
    }

    public int getCookingOil() {
        return cookingOil;
    }

    public void setCookingOil(int cookingOil) {
        this.cookingOil = cookingOil;
    }

    public int getSpices() {
        return spices;
    }

    public void setSpices(int spices) {
        this.spices = spices;
    }
}