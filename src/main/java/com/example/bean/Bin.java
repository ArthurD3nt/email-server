package com.example.bean;

import java.io.Serializable;

public class Bin implements Serializable{
    private String id;

    private String email;

    public Bin(String id, String email) {
        this.id = id;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

