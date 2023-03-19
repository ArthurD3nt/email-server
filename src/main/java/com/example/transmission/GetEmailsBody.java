package com.example.transmission;

import java.sql.Timestamp;
import java.util.Date;

public class GetEmailsBody extends BaseBody{

    private Timestamp timestamp;

    public GetEmailsBody(String email,Timestamp timestamp) {
        super(email);
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String date) {
        this.timestamp = new java.sql.Timestamp(new Date(date).getTime());
    }
}
