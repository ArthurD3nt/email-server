package com.example.servermail;

import com.example.bean.Communication;
import com.example.bean.Email;
import com.example.bean.User;
import com.example.model.UserModel;

import java.io.*;
import java.net.*;
import java.util.*;


public class ServerHandler implements Runnable{

    private Socket socket;
    private int counter;

    private ArrayList<Email> emailArrayList;

    public ServerHandler(Socket socket, int counter) {
        this.socket = socket;
        this.counter = counter;
    }

    private Communication connection(String email){
        UserModel.getInstance().readObjectFromFile(email);
        this.emailArrayList = UserModel.getInstance().getUser(email);

        Communication response = new Communication("connection_ok", emailArrayList);

        return response;
    }


    private void sendEmail(Email email){
        System.out.println(email.toString());
        UserModel.getInstance().addEmailToMap(email.getSender(),email);
        for(String user : email.getReceivers())
            UserModel.getInstance().addEmailToMap(user,email);
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
                    while(true) {

                        Communication communication = (Communication) in.readObject();
                        switch (communication.getAction()) {
                            case "connection":
                                out.writeObject(connection((String)communication.getBody()));
                                break;
                            case "send_email":
                                sendEmail((Email)communication.getBody());
                                out.writeObject(new Communication("emails_saved", new ArrayList<>()));
                                break;
                        }
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
