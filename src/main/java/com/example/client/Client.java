package com.example.client;

import com.example.bean.Communication;
import com.example.bean.Email;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private String userEmail;

    private ArrayList<Email> emailArrayList;

    private static Socket socket;


    public Client(String userEmail, ArrayList<Email> emailArrayList) throws IOException {
        this.userEmail = userEmail;
        this.emailArrayList = emailArrayList;
        this.socket = new Socket( InetAddress.getLocalHost().getHostName(),8189);
    }

    private void connection() throws IOException, ClassNotFoundException {
        OutputStream outStream = socket.getOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(outStream);

        Communication c = new Communication("connection",this.userEmail);
        out.writeObject(c);

        /*inStream mi serve per ricevere la risposta del server*/
        ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
        Communication communication = (Communication) inStream.readObject();

    }

    private void sendEmail(){
        Email e = (new Email("matteo", List.of("aaa"),"test","questo è un test"));
        Communication email = new Communication("sendEmail",e);
    }

}
