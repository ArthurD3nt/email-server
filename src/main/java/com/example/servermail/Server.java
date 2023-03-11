package com.example.servermail;
import com.example.bean.Email;
import com.example.bean.User;
import com.example.model.LogModel;
import com.example.model.UserModel;

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
             Runnable r = new ServerHandler(incoming, i, logModel);
             new Thread(r).start();
             i++;
         }
     }
     catch (IOException e) {e.printStackTrace();}
    }
}
