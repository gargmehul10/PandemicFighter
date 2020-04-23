package com.mehul.pandemicfighter;

public class User {

    private  String name;
    private  String email;
    private  String profession;

    public User(){

    }

    public User(String name, String email, String profession) {
        this.name = name;
        this.email = email;
        this.profession = profession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }
}