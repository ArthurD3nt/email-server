package com.example.server.bean;

public class BooleanBody  extends BaseBody {
    private boolean result ;


    public BooleanBody(String email, boolean result) {
        super(email);
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
