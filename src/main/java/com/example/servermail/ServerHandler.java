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
            // create a DataInputStream so we can read data from it.
            InputStream inputStream = incoming.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            OutputStream outputStream = incoming.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            // read the message from the socket
            String email = dataInputStream.readUTF();
            File folder = new File("./email/" + email);
            if (!folder.exists()) {
                dataOutputStream.write(1);
                System.out.println("No email");
                // create the folder
                folder.mkdir();
            }
            else {
                dataOutputStream.write(1);
                System.out.println("Email found");
            }

            // check if exist a folder with the name of the sender
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
