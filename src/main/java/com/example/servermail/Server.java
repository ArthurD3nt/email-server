package com.example.servermail;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{
    public Server() {}

    @Override
    public void run (){
     System.out.println("Finestra del server: ");
     try {
         int i = 1;
         ServerSocket s = new ServerSocket(8189);

         while (true) {
             Socket incoming = s.accept();
             System.out.println("Client number: " + i);
             Runnable r = new ServerHandler(incoming, i);
             new Thread(r).start();
             i++;
         }
     }
     catch (IOException e) {e.printStackTrace();}
    }
}
