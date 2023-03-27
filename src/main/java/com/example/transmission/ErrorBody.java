package com.example.transmission;
public class ErrorBody extends BaseBody {
    private String error;
    public ErrorBody(String email, String error) {
        super(email);
        this.error = error;
    }
}
