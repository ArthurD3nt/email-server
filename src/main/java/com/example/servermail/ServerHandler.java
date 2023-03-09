package com.example.servermail;

import com.example.bean.Communication;
import com.example.bean.Email;

import java.io.*;
import java.net.*;
import java.util.*;


public class ServerHandler implements Runnable{

    private Socket socket;
    private int counter;

    private  Communication communication = null;


    public ServerHandler(Socket socket, int counter) {
        this.socket = socket;
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
                InputStream inStream = socket.getInputStream();
                ObjectInputStream in = new ObjectInputStream(inStream);

                OutputStream outStream = socket.getOutputStream();
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
                socket.close();
            }
        }
        catch (IOException e) {e.printStackTrace();}
    }


}
