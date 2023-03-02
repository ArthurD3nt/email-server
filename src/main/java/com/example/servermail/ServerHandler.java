package com.example.servermail;

import java.io.*;
import java.net.*;
import java.util.*;


public class ServerHandler implements Runnable{

    private Socket incoming;
    private int counter;


    public ServerHandler(Socket incoming, int counter) {
        this.incoming = incoming;
        this.counter = counter;
    }

    @Override
    public void run() {
        try {
            try {
                InputStream inStream = incoming.getInputStream();
                OutputStream outStream = incoming.getOutputStream();

                Scanner in = new Scanner(inStream);
                PrintWriter out = new PrintWriter(outStream, true);

                out.println("Ciao client " + this.counter);

                boolean done = false;
                while (!done /*&& in.hasNextLine()*/) {
                    String line = in.nextLine();

                    System.out.println("ECHO: "+ line);
                    if (line.trim().equals("BYE"))
                        done = true;
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
