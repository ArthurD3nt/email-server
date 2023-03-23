package com.example.server.servermail;

import com.example.transmission.*;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.ArrayList;

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

    private static final String EMAIL_TO_USE = "matteo@edu.unito.it";

    private static Communication testConnection(String email){
       return new Communication("connection",new BaseBody(email));
    }
    private static Communication get_bin_emails(String email){
       return new Communication("get_sent_emails",new BaseBody(email));
    }
    private static Communication sendEmail(){
        ArrayList<String> receivers = new ArrayList<>();
       receivers.add("ale@edu.unito.it");
        receivers.add("email@email.com");
       receivers.add("fra@edu.unito.it");
        EmailBody e = new EmailBody("matteo@edu.unito.it",receivers, "email 4", "testo email 4" );
        return new Communication("send_email", e);
    }

    private static Communication getEmails(){
        Timestamp t = Timestamp.valueOf("2023-03-14 22:40:31.370");
        GetEmailsBody g = new GetEmailsBody(EMAIL_TO_USE,t);
        return new Communication("get_emails", g);
    }

    private static Communication moveToBin() {
        BinBody b = new BinBody("751d92b7-10ab-4f59-9fc7-bcd83e460215", EMAIL_TO_USE);

        return new Communication("bin", b);
    }

    private static Communication delete(){
        BaseBody b = new BaseBody(EMAIL_TO_USE);
        return new Communication("delete",b);
    }
    public static void main(String[] args) {
        try {
            String nomeHost = InetAddress.getLocalHost().getHostName();

            Socket s = new Socket(nomeHost, 8189);

            try {
                OutputStream outStream = s.getOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(outStream);
                // Communication c = sendEmail();
                 Communication c = testConnection(EMAIL_TO_USE);
                // Communication c = get_bin_emails(EMAIL_TO_USE);
                // Communication c = getEmails();
                // Communication c = moveToBin();
                // Communication c = delete();
                out.writeObject(c);


                ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());
                try {
                        Communication communication = (Communication) inStream.readObject();
                        switch(communication.getAction()){
                            case "connection_ok":
                                System.out.println("connection_ok");
                                System.out.println(((ConnectionBody)communication.getBody()).getEmails());
                                break;
                            case "get_sent_emails_ok":
                                System.out.println("get_sent_emails_ok");
                                System.out.println(communication.getBody());
                                break;
                            case "get_bin_emails_ok":
                                System.out.println("get_bin_emails_ok");
                                System.out.println(communication.getBody());
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
                            case "delete_permanently_ok":
                                System.out.println("delete");
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
