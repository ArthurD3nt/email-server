package com.example.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String user;

    private ArrayList<Email> emails;

    public User(String user, ArrayList<Email> emails) {
        this.user = user;
        this.emails = emails;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ArrayList<Email> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<Email> emails) {
        this.emails = emails;
    }
}
