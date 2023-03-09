package com.example.servermail;

import com.example.bean.Communication;
import com.example.bean.Email;

import java.io.*;
import java.net.*;
import java.util.*;


public class Client {
    public static void main(String[] args) {
        try {
            String nomeHost = InetAddress.getLocalHost().getHostName();

            Socket s = new Socket(nomeHost, 8189);

            try {
                OutputStream outStream = s.getOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(outStream);

                Communication c = new Communication("connection","email@email.com");
                out.writeObject(c);

                ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());

                try {
                    Communication communication = (Communication) inStream.readObject();

                    switch(communication.getAction()){
                        case "ack":
                            System.out.println("ack");
                            break;
                        case "sendEmail":
                            Email e=(new Email("matteo",List.of("aaa"),"test","questo è un test"));
                            //Communication c= new Communication("sendEmail",e);
                            System.out.println("body:"+communication.getBody());
                            break;
                    }
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }


            }
            finally {
                s.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
