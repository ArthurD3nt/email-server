package com.example.servermail;

import com.example.bean.Communication;
import com.example.bean.Email;
import com.example.bean.User;
import com.example.model.UserModel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable{

    @Override
    public void run (){

     try {
         int i = 0;
         ServerSocket s = new ServerSocket(8189);

         while (true) {
             Socket incoming = s.accept();
             Runnable r = new ServerHandler(incoming, i);
             new Thread(r).start();
             i++;
         }
     }
     catch (IOException e) {e.printStackTrace();}
    }
}
