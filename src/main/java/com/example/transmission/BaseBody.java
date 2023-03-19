package com.example.server.bean;

import java.io.Serializable;

public class BaseBody implements Serializable {
    protected String email;

    public BaseBody(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
