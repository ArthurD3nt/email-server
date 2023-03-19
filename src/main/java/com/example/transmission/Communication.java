package com.example.transmission;

import java.io.Serializable;

public class Communication implements Serializable {
    private String action;

    private BaseBody body;

    public Communication(String action, BaseBody body) {
        this.action = action;
        this.body = body;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public BaseBody getBody() {
        return body;
    }

    public void setBody(BaseBody body) {
        this.body = body;
    }
}
