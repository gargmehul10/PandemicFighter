package com.mehul.pandemicfighter.Module3;

public class Transaction {
    private String timestamp;
    private double rice, wheatFlour, cookingOil, sugar;
    private boolean complete;
    public Transaction()
    {

    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Transaction(String timestamp, double rice, double wheatFlour, double cookingOil, double sugar) {
        this.timestamp = timestamp;
        this.rice = rice;
        this.wheatFlour = wheatFlour;
        this.cookingOil = cookingOil;
        this.sugar = sugar;
        this.complete = false;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getRice() {
        return rice;
    }

    public void setRice(double rice) {
        this.rice = rice;
    }

    public double getFlour() {
        return wheatFlour;
    }

    public void setFlour(double wheatFlour) {
        this.wheatFlour = wheatFlour;
    }

    public double getCookingOil() {
        return cookingOil;
    }

    public void setCookingOil(double cookingOil) {
        this.cookingOil = cookingOil;
    }

    public double getSugar() {
        return sugar;
    }

    public void setSugar(double sugar) {
        this.sugar = sugar;
    }
}