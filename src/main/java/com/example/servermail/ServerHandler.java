package com.example.servermail;

import com.example.bean.Communication;
import com.example.bean.Email;
import com.example.bean.User;

import java.io.*;
import java.net.*;
import java.util.*;


public class ServerHandler implements Runnable{

    private Socket socket;
    private int counter;

    private  Communication communication = null;

    private Server server;

    private ArrayList<User> userArrayList;

    public ServerHandler(Socket socket, int counter) {
        this.socket = socket;
        this.counter = counter;
        this.server = new Server();
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
                            this.userArrayList = this.server.getUser();
                            System.out.println("connection");

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


                                        /*switch(action){
                case "connection":
                   for(User u : this.user){

                       if(u.getUser().equals(user)){
                           found = true;
                           communication = new Communication("connection_ok", u.getEmails());
                       }
                   }
            }

            if(!found){
                this.user.add(new User(user,new ArrayList<Email>()));
                this.writeObjectToFile();
                communication  = new Communication("created_email", new ArrayList<Email>());
            }*/
