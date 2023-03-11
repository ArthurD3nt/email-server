package com.example.servermail;

import com.example.bean.Communication;
import com.example.bean.Email;
import com.example.bean.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable{
    private ArrayList<User> user;

    /**
     * final:
     * static:
     */
    private final static String FILE_PATH_MAC = "/Users/matteomarengo/Documents/uni/email-server/src/main/java/com/example/email/email.txt";

    public Server() {
        //User u = new User("matteo@email.com", new ArrayList<Email>());
        this.user = new ArrayList<User>();
        //this.user.add(u);
        //this.writeObjectToFile();
    }

    /* questa classe scrive su file tutto l'array di user */
    public synchronized void writeObjectToFile() {
        /**
         * try-with-resources: tu fai il try, dichiari la variabile dentro il try e
         * fa la close in automatico senza che tu debba farla nel codice
         */
        try (ObjectOutputStream objectOut = new ObjectOutputStream(new FileOutputStream(FILE_PATH_MAC))) {
            objectOut.writeObject(this.user);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private synchronized void readObjectFromFile(){

        try(ObjectInputStream objectInput = new ObjectInputStream((new FileInputStream(FILE_PATH_MAC)))){
            this.user = (ArrayList<User>)objectInput.readObject();

        } catch (FileNotFoundException fileNotFoundException) {
            throw new RuntimeException(fileNotFoundException);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        } catch (ClassNotFoundException notFoundException) {
            throw new RuntimeException(notFoundException);
        }
    }

    public ArrayList<User> getUser() {
        return user;
    }


    @Override
    public void run (){

     try {
         int i = 1;
         ServerSocket s = new ServerSocket(8189);

         this.readObjectFromFile();

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
