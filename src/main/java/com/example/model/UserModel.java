package com.example.model;

import com.example.bean.Email;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/*
 Pattern singleton: il costruttore è privato, viene istanziato con getIstance() quando è null altrimenti ritorna instance
 */
public class UserModel {
    private static UserModel instance = null;

    /*Forse è più comodo avere HashMap<string email, ArrayList<Email>()> teoricamente è più performante*/
    private HashMap<String,ArrayList<Email>> mapUserEmail = new HashMap<>(); //se non lo vuole mettiamo le if
    private final static String FILE_PATH_MAC = "/Users/matteomarengo/Documents/uni/email-server/src/main/java/com/example/email/";

    private UserModel() {
    }

    public static UserModel getInstance(){
        if(instance == null)
            instance = new UserModel();
        return instance;
    }

    /* questa classe scrive su file tutto l'array di user */
    public synchronized void writeObjectToFile(String email) {
        /**
         * try-with-resources: tu fai il try, dichiari la variabile dentro il try e
         * fa la close in automatico senza che tu debba farla nel codice
         */
        try (ObjectOutputStream objectOut = new ObjectOutputStream(new FileOutputStream(FILE_PATH_MAC+email.hashCode()+".txt"))) {
            objectOut.writeObject(this.mapUserEmail.get(email));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addEmailToMap(String user, Email email){
       ArrayList<Email> emails = this.getUser(user);
       emails.add(email);
       this.mapUserEmail.put(user,emails);
       this.writeObjectToFile(user);
    }

    public synchronized void readObjectFromFile(String email){
        try(ObjectInputStream objectInput = new ObjectInputStream((new FileInputStream(FILE_PATH_MAC+email.hashCode()+".txt")))){
            this.mapUserEmail.put(email,(ArrayList<Email>)objectInput.readObject());
        } catch ( IOException | ClassNotFoundException fileNotFoundException) {
            this.mapUserEmail.put(email, new ArrayList<>());
        }
    }

    public ArrayList<Email> getUser(String email){
        if(mapUserEmail.containsKey(email))
            return mapUserEmail.get(email);
        return new ArrayList<Email>();
    }
}
