package com.example.matthias_pc.challengeme.model;

public class User {

    private String email,status;

    public User(){

    }


    public User(String email, String status) {
        this.email = email;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }
}
