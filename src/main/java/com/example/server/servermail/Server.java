package com.example.server.servermail;
import com.example.server.model.LogModel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    public LogModel logModel;

    public Server(LogModel log) {
        this.logModel = log;
    }

    @Override
    public void run (){

     try {
         int i = 0;
         ServerSocket s = new ServerSocket(8189);

         while (true) {
             Socket incoming = s.accept();
             logModel.setLog("thread: "+i);
             Runnable r = new ServerHandler(incoming, i, logModel);
             new Thread(r).start();
             i++;
         }
     }
     catch (IOException e) {e.printStackTrace();}
    }
}
