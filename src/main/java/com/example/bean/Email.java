package com.example.bean;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Email implements Serializable {

    private String sender;
    private List<String> receivers;
    private String subject;
    private String text;

    private Date datetime;

    private Email() {}

    public Email(String sender, List<String> receivers, String subject, String text) {
        this.sender = sender;
        this.subject = subject;
        this.text = text;
        this.receivers = new ArrayList<>(receivers);
    }

    public String getSender() {
        return sender;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }
}

