package com.example.transmission;

public class BinBody extends BaseBody {
    private String id;


    public BinBody(String id, String email) {
        super(email);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

