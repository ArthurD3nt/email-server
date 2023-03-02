package com.example.servermail;

import java.io.*;
import java.net.*;
import java.util.*;


public class Client {
    public static void main(String[] args) {
        try {
            String nomeHost = InetAddress.getLocalHost().getHostName();

            Socket s = new Socket(nomeHost, 8189);

            try {
                InputStream inStream = s.getInputStream();
                Scanner in = new Scanner(inStream);
                OutputStream outStream = s.getOutputStream();
                PrintWriter out = new PrintWriter(outStream, true);
                Scanner stin = new Scanner(System.in);

                String line = in.nextLine(); // attenzione: se il server non scrive nulla questo resta in attesa...
                System.out.println(line);

                boolean done = false;
                while (!done) /* && in.hasNextLine()) */ {

                    String lineout = stin.nextLine();
                    out.println(lineout);

                    System.out.println(line);
                    if (lineout.equals("BYE"))
                        done = true;
                }
            }
            finally {
                s.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
