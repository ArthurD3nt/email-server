package com.example.servermail;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerHandler implements Runnable {

    private Socket incoming;
    private int counter;

    public ServerHandler(Socket incoming, int counter) {
        this.incoming = incoming;
        this.counter = counter;
    }

    @Override
    public void run() {
        try{
            ObjectInputStream in = new ObjectInputStream(incoming.getInputStream());  
            ObjectOutputStream out = new ObjectOutputStream(incoming.getOutputStream()); 
            
            Map<String, Object> jsonMap = (Map<String, Object>) in.readObject();
            String connection_type = (String) jsonMap.get("type");
            switch(connection_type){
                case "connection":
                    String email = (String) jsonMap.get("email");
                    // check if exist folder with email name
                    File folder = new File("./email/", email);
                    String response = "1";
                    if(!folder.exists()){
                        response = "1";
                    }
                    else{
                        response = "0";
                    }
                    Map<String, String> jsonRes = new HashMap<>();
                    jsonRes.put("response", response);
                    out.writeObject(jsonRes);
                    }
            
            

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
