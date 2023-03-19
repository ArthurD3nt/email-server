package com.example.server.bean;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class EmailListBody extends BaseBody {
    private ArrayList<EmailBody> emails;

    public EmailListBody(String email, ArrayList<EmailBody> emails) {
        super(email);
        this.emails = emails;
    }

    public ArrayList<EmailBody> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<EmailBody> emails) {
        this.emails = emails;
    }

    @Override
    public String toString() {
        return "EmailListBody{" +
                "emails=" + emails.stream().map(email->email.toString()).collect(Collectors.joining(", ")) +
                '}';
    }
}
