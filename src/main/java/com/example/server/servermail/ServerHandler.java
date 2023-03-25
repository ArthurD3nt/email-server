package com.example.server.servermail;

import com.example.server.model.LogModel;
import com.example.server.model.UserService;
import com.example.transmission.*;

import java.io.*;
import java.net.*;
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
        logModel.setLog("Client " + email + " connected");
        User user = userService.readUserFromFile(email);

        if(user == null){
            logModel.setLog("Client " + email + ":null, created!");
            user = userService.createUser(email);
        }

        ArrayList<ArrayList<EmailBody>> arrayLists = new ArrayList<>();
        ArrayList<EmailBody> emailReceived = new ArrayList<>();
        ArrayList<EmailBody> emailSent = new ArrayList<>();
        ArrayList<EmailBody> emailBin = new ArrayList<>();

        user.getEmails().forEach(emailBody -> {
           if(emailBody.getReceivers().contains(email) && emailBody.getSender().equals(email) && !emailBody.getBin())
           {
               emailReceived.add(emailBody);
               emailSent.add(emailBody);
           }
           else if(emailBody.getReceivers().contains(email) && !emailBody.getBin()){
               emailReceived.add(emailBody);
           }
           else if(emailBody.getSender().equals(email) && !emailBody.getBin()){
               emailSent.add(emailBody);
           }
           else if(emailBody.getBin()){
               emailBin.add(emailBody);
           }
        });

        arrayLists.add(emailReceived);
        arrayLists.add(emailSent);
        arrayLists.add(emailBin);

        Communication response = new Communication("connection_ok", new ConnectionBody(user.getUser(),arrayLists));

        out.writeObject(response);
    }

    private void getSentEmails(String email) throws IOException {
        User user = userService.readUserFromFile(email);
        if(user == null){
            user = userService.createUser(email);
        }
        ArrayList<EmailBody> emailBodies = new ArrayList<>();
        user.getEmails().forEach(emailBody -> {
            if(emailBody.getSender().equals(email) && !emailBody.getBin()){
                emailBodies.add(emailBody);
            }
        });

        Communication response = new Communication("get_sent_emails_ok", new EmailListBody(user.getUser(),emailBodies));
        logModel.setLog("Function getSentEmails: to: " + email);
        out.writeObject(response);
    }

    private void getBinEmails(String email) throws IOException {
        User user = userService.readUserFromFile(email);
        if(user == null){
            user = userService.createUser(email);
        }
        ArrayList<EmailBody> emailBodies = new ArrayList<>();
        user.getEmails().forEach(emailBody -> {
            if(emailBody.getBin()){
                emailBodies.add(emailBody);
            }
        });

        Communication response = new Communication("get_bin_emails_ok", new EmailListBody(user.getUser(),emailBodies));
        logModel.setLog("Function getBinEmails to: " + email);
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
            else if(receiver.equals(email.getSender())) {
                userService.unlock(receiver);
                logModel.setLog("RECEIVER: "+ receiver + " uguale a sender: " + email.getSender());
                continue;
            }
            userReceiver.getEmails().add(email);
            userService.writeUserToFile(userReceiver);
        }

        out.writeObject(new Communication("emails_saved",new BooleanBody(sender.getUser(),true)));
    }

    private void getNewEmails(GetEmailsBody getEmailsBody) throws IOException {
        User user = userService.readUserFromFile(getEmailsBody.getEmail());
        logModel.setLog("Get email by: " + getEmailsBody.getEmail());

        ArrayList<EmailBody> emailBodies = new ArrayList<>();
        for(EmailBody e: user.getEmails()){
            if(e.getTimestamp().after(getEmailsBody.getTimestamp()) && (!getEmailsBody.getEmail().equals(e.getSender())) || e.getSender().equals(e.getReceivers().get(0))) {
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
        int i = 0;;

        if(user.getEmails().size() == 0 ){
            out.writeObject(new Communication("delete_permanently_not_ok", new BooleanBody(email, false)));
        }

        while(i < user.getEmails().size()){
            if(user.getEmails().get(i).getBin()){
                logModel.setLog("Client: "+ email +" remove email with id " + user.getEmails().get(i).getId());
                user.getEmails().remove(i);
            }
            else { i++;}
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
                            case "get_sent_emails":
                                getSentEmails(communication.getBody().getEmail());
                                break;
                            case "get_bin_emails":
                                getBinEmails(communication.getBody().getEmail());
                                break;
                            case "get_new_emails":
                                getNewEmails((GetEmailsBody)communication.getBody());
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
