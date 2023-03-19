package com.example.transmission;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String user;

    private ArrayList<EmailBody> emails;

    public User(String user, ArrayList<EmailBody> emails) {
        this.user = user;
        this.emails = emails;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ArrayList<EmailBody> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<EmailBody> emails) {
        this.emails = emails;
    }
}
