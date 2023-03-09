package com.example.servermail;

import com.example.bean.Communication;
import com.example.bean.Email;

import java.io.*;
import java.net.*;
import java.util.*;


public class ServerHandler implements Runnable{

    private Socket incoming;
    private int counter;

    private  Communication communication = null;


    public ServerHandler(Socket incoming, int counter) {
        this.incoming = incoming;
        this.counter = counter;
    }

    @Override
    public void run() {
        try {
            try {
                /* Stream sono un flusso di dati
                * InputStream: sono i byte che ricevo in input
                * OutputStream: sono i byte che invio in input
                * */

                InputStream inStream = incoming.getInputStream();
                OutputStream outStream = incoming.getOutputStream();

                ObjectInputStream in = new ObjectInputStream(inStream);
                ObjectOutputStream out= new ObjectOutputStream(outStream);

                try {

                communication = (Communication) in.readObject();

                switch(communication.getAction()){
                    case "connection":
                        System.out.println(communication.getBody());
                        out.writeObject(new Communication("ack",""));
                        break;
                    case "sendEmail":
                        System.out.println("body:"+communication.getBody());
                        break;
                }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
            finally {
                System.out.println("FINITO");
                incoming.close();
            }
        }
        catch (IOException e) {e.printStackTrace();}
    }


}
