package com.example.servermail;

import com.example.bean.*;
import com.example.model.LogModel;
import com.example.model.UserService;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class ServerHandler implements Runnable{

    private Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;
    private int counter;
    private LogModel logModel;
    private UserService userService;

    public ServerHandler(Socket socket, int counter, LogModel logModel) {
        this.logModel = logModel;
        this.socket = socket;
        this.counter = counter;
        this.userService = UserService.getInstance();
    }

    private void connection(String email) throws IOException {
        User user = userService.readUserFromFile(email);
        if(user == null){
            user = userService.createUser(email);
        }

        Communication response = new Communication("connection_ok", new EmailListBody(user.getUser(),user.getEmails()));
        logModel.setLog("Client " + email + " connected");
        out.writeObject(response);
    }

    private void sendEmail(EmailBody email) throws IOException {
        // blocco il file del sender
        logModel.setLog("User "+email.getSender()
                + " sending email: "+email.getId()
                + " to: " + email.getReceivers().stream().collect(Collectors.joining(", ")));

        User sender = userService.readUserFromFileBlocking(email.getSender());

        if(sender == null){
            userService.unlock(email.getSender());
            //gestione sender non esistente: fatela voi a me è successo non so come
            logModel.setLog("ERRORE: utente non trovato");
            out.writeObject(new Communication("emails_not_saved",new BooleanBody(sender.getUser(),false)));
            return;
        }

        sender.getEmails().add(email);
        userService.writeUserToFile(sender);

        for (String receiver:email.getReceivers()) {
            User userReceiver = userService.readUserFromFileBlocking(receiver);
            if(userReceiver == null){
                userService.unlock(receiver);
                logModel.setLog("ERRORE: "+ receiver + " non trovato");
                //gestione utente non esistente: fatela voi
                continue;
            }
            userReceiver.getEmails().add(email);
            userService.writeUserToFile(userReceiver);
        }

        out.writeObject(new Communication("emails_saved",new BooleanBody(sender.getUser(),true)));
    }

    private void get_emails(GetEmailsBody getEmailsBody) throws IOException {
        User user = userService.readUserFromFile(getEmailsBody.getEmail());
        logModel.setLog("Get email by: " + getEmailsBody.getEmail());
        /*System.out.println(getEmailsBody.getTimestamp());
        System.out.println(Timestamp.valueOf("2023-03-14 22:40:31.371"));
        System.out.println(getEmailsBody.getTimestamp().equals(Timestamp.valueOf("2023-03-14 22:40:31.371")));*/

        ArrayList<EmailBody> emailBodies = new ArrayList<>();
        for(EmailBody e: user.getEmails()){
            if(e.getTimestamp().after(getEmailsBody.getTimestamp())) {
              emailBodies.add(e);
            }
        }

        out.writeObject(new Communication("get_emails_ok",new EmailListBody(getEmailsBody.getEmail(), emailBodies)));
    }

    private void moveToBin(String id, String email) throws IOException{
        User user = userService.readUserFromFileBlocking(email);
        for(EmailBody e: user.getEmails()){
            if(e.getId().equals(id)) {
                e.setBin(true);
                logModel.setLog("Client: "+ email +" moved to bin email with id " + id );
            }
        }
        userService.writeUserToFile(user);
        out.writeObject(new Communication("bin_ok",new BooleanBody(email, true)));

    }

    private void deletePermanently(String email) throws IOException {
        User user = userService.readUserFromFileBlocking(email);
        int i;
        if(user.getEmails().size() == 0 ){

            out.writeObject(new Communication("delete_permanently_ok", new BooleanBody(email, false)));
        }
        for(i = 0; i < user.getEmails().size(); i++){
            if(user.getEmails().get(i).getBin()){
                user.getEmails().remove(i);
                //logModel.setLog("Client: "+ email +" remove email with id " + e.getId());
            }
        }
        userService.writeUserToFile(user);

        out.writeObject(new Communication("delete_permanently_ok", new BooleanBody(email, true)));
    }

    @Override
    public void run() {

        try {
            try {
                /* Stream sono un flusso di dati
                * InputStream: sono i byte che ricevo in input
                * OutputStream: sono i byte che invio in input
                * */
                InputStream inStream = socket.getInputStream();
                in = new ObjectInputStream(inStream);
                OutputStream outStream = socket.getOutputStream();
                out= new ObjectOutputStream(outStream);

                try {
                        Communication communication = (Communication) in.readObject();

                        switch (communication.getAction()) {
                            case "connection":
                                connection(communication.getBody().getEmail());
                                break;
                            case "get_emails":
                                get_emails((GetEmailsBody)communication.getBody());
                                break;
                            case "send_email":
                                sendEmail((EmailBody) communication.getBody());
                                break;
                            case "bin":
                                BinBody b = (BinBody) communication.getBody();
                                moveToBin(b.getId(), b.getEmail());
                                break;
                            case "delete":
                                deletePermanently(communication.getBody().getEmail());
                                break;
                        }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
            finally {
                System.out.println("FINITO");
                logModel.setLog("Client disconnected");
                socket.close();
            }
        }
        catch (IOException e) {e.printStackTrace();}
    }

}
