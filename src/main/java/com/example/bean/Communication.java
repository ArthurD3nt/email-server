package com.example.bean;

import java.io.Serializable;

public class Communication implements Serializable {
    private String action;

    private Serializable body;

    public Communication(String action, Serializable body) {
        this.action = action;
        this.body = body;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Serializable getBody() {
        return body;
    }

    public void setBody(Serializable body) {
        this.body = body;
    }
}
