package com.example.servermail;

import com.example.bean.Communication;
import com.example.bean.Email;

import java.io.*;
import java.net.*;
import java.util.ArrayList;


/**
 * connection
 *      --> connessione riuscita:
 *          --> {action: created_email,emails: []}
 *          --> {action: connection_ok, emails: [{receive: [{...}]},{sent: [{...}]},{bin: [{...}]}]}
 *      --> try - catch: server non disponibile
 *
 * send_email:
 *      --> {action: send_email_ok, emails:[]}
 *      --> {action: not_ok, emails:[]}
 *
 * scheduler:
 *      --> lo scheduler invia l'email ricevute dal client con timestamp maggiore dell'ultimo timestamp salvato,
 *          gira ogni tot secondi solo sui
 *      --> {action: scheduler, emails:[{}]}
 *
 * bin:
 *      --> il client sposta in locale l'email eliminata, il server cerca l'email e mette bin = true
 *      --> {action: bin_ok, emails:[]}
 *      --> {action: bin_not_ok, emails:[]}
 *
 *  delete:
 *      --> server: elimina tutte le email con bin: true, il client svuota l'arrayList solo se l'eliminazione è andata a buon fine
 *      -->
 */

public class ClientMain {
    public static void main(String[] args) {
        try {
            String nomeHost = InetAddress.getLocalHost().getHostName();

            Socket s = new Socket(nomeHost, 8189);

            try {
                OutputStream outStream = s.getOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(outStream);

                Communication c = new Communication("connection","email@email.com");
                out.writeObject(c);

                ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());

                try {
                    while(true){

                        Communication communication = (Communication) inStream.readObject();

                        switch(communication.getAction()){
                            case "connection_ok":
                                System.out.println("connection_ok");
                                System.out.println(communication.getBody());
                                /*ArrayList<String> receivers = new ArrayList<>();
                                receivers.add("matteo@edu.unito.it");
                                receivers.add("stefano@edu.unito.it");
                                receivers.add("ale@edu.unito.it");
                                Email e  = new Email((String)c.getBody(), receivers, "first email", "fisrt-email text");
                                out.writeObject(new Communication("send_email", e));*/
                                break;
                            case "emails_saved":
                                System.out.println("email salvate");
                                break;
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
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
