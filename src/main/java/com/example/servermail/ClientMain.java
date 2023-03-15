package com.example.servermail;

import com.example.bean.BaseBody;
import com.example.bean.Communication;
import com.example.bean.EmailBody;
import com.example.bean.GetEmailsBody;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * connection
 *     fatta --> connessione riuscita:
 *          --> {action: created_email,emails: []}
 *          --> {action: connection_ok, emails: [{receive: [{...}]},{sent: [{...}]},{bin: [{...}]}]}
 *      --> try - catch: server non disponibile
 *
 * send_email:
 *     fatta --> {action: send_email_ok, emails:[]}
 *      --> {action: not_ok, emails:[]}
 *
 * scheduler:
 *      --> lo scheduler invia l'email ricevute dal client con timestamp maggiore dell'ultimo timestamp salvato,
 *          gira ogni tot secondi solo sui client connessi
 *      --> {action: scheduler, emails:[{}]}
 *
 * bin:
 *      --> il client sposta in locale l'email eliminata, il server cerca l'email e mette bin = true
 *     fatta --> {action: bin_ok, emails:[]}
 *      --> {action: bin_not_ok, emails:[]}
 *
 *  delete:
 *      --> server: elimina tutte le email con bin: true, il client svuota l'arrayList solo se l'eliminazione è andata a buon fine
 *
 */

public class ClientMain {
    private static Communication testConnection(String email){
       return new Communication("connection",new BaseBody(email));
    }
    private static Communication sendEmail(){
        ArrayList<String> receivers = new ArrayList<>();
        receivers.add("stefano@edu.unito.it");
        receivers.add("matteo@edu.unito.it");
        receivers.add("fede@edu.unito.it");
        EmailBody e = new EmailBody("email@email.com",receivers, "email 2", "testo email 2" );
        return new Communication("send_email", e);
    }

    private static Communication getEmails(){
        Timestamp t = Timestamp.valueOf("2023-03-14 22:40:31.370");
        GetEmailsBody g = new GetEmailsBody("fede@edu.unito.it",t);
        return new Communication("get_emails", g);
    }

    public static void main(String[] args) {
        try {
            String nomeHost = InetAddress.getLocalHost().getHostName();

            Socket s = new Socket(nomeHost, 8189);

            try {
                OutputStream outStream = s.getOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(outStream);
                //Communication c = sendEmail();
                // Communication c = testConnection("fede@edu.unito.it");
                Communication c = getEmails();
                out.writeObject(c);

                ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());
                try {
                        Communication communication = (Communication) inStream.readObject();
                        switch(communication.getAction()){
                            case "connection_ok":
                                System.out.println("connection_ok");
                                System.out.println(communication.getBody());
                                /*Bin b = new Bin("71a3cf8f-5a26-49d6-93bd-53888bc2fc22", "email@email.com");
                                out.writeObject(new Communication("bin", b));*/
                                break;
                            case "emails_saved":
                                System.out.println("email salvate");
                                break;
                            case "get_emails_ok":
                                System.out.println("Get email");
                                System.out.println(communication.getBody());
                                break;
                            case "bin_ok":
                                System.out.println("bin ok");
                                break;
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
