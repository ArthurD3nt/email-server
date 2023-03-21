package com.example.server.model;

import com.example.transmission.User;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 Pattern singleton: il costruttore è privato, viene istanziato con getIstance() quando è null altrimenti ritorna instance
 */
public class UserService {
    private static UserService instance = null;

    private static HashMap<String,ReentrantReadWriteLock > filesLock = new HashMap<>();


    private final static String FILE_PATH_MAC = "/Users/matteomarengo/Documents/uni/email-server/src/main/java/com/example/server/email/";
    private final static String FILE_PATH_MINT = "/home/ale/github/email-server/src/main/java/com/example/email";
    private final static String FILE_PATH_WIN = "C:\\Users\\Stefano\\Desktop\\excentio\\test\\email-server-main\\src\\main\\java\\com\\example\\email\\";

    private String FILE_PATH_TO_USE = FILE_PATH_MAC;
    private UserService() {

    }

    public static synchronized UserService getInstance(){
        if(instance == null)
            instance = new UserService();
        return instance;
    }

    /* questa classe scrive su file tutto l'array di user */
    public User createUser(String email){
        blockWrite(email);
        User u=new User(email,new ArrayList<>());
        writeUserToFile(u);
        return u;
    }
    public User readUserFromFile(String email){
        Lock read = blockRead(email);
        read.lock();
        try(ObjectInputStream objectInput = new ObjectInputStream((new FileInputStream(FILE_PATH_TO_USE+email.toLowerCase()+".txt")))){
            return (User)objectInput.readObject();
        } catch ( IOException | ClassNotFoundException fileNotFoundException) {
            /*TODO: GESTIRE CASO DI ERRORE*/
        }finally {
            read.unlock();
        }
        return null;

    }

    public User readUserFromFileBlocking(String email){
        blockWrite(email);
        try(ObjectInputStream objectInput = new ObjectInputStream((new FileInputStream(FILE_PATH_TO_USE+email.toLowerCase()+".txt")))){
            return (User)objectInput.readObject();
        } catch ( IOException | ClassNotFoundException fileNotFoundException) {

        }
        return null;

    }

    private synchronized Lock blockRead(String email){
        Lock read;
        /**
         * Controlla che sia presente il lock associato all'email:
         * se presente: associa a read il lock e lo restituisce
         * se non presente crea un lock, lo inserisce nella mappa filesLock e associa a read il lock e lo restituisce
         * */
        if(filesLock.containsKey(email)){
            read = filesLock.get(email).readLock();
        }else{
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
            filesLock.put(email,lock);
            read = lock.readLock();
        }
        return read;
    }
    private synchronized void blockWrite(String email){
        Lock write;
        if(filesLock.containsKey(email)){
            write = filesLock.get(email).writeLock();
        }else{
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
            filesLock.put(email,lock);
            write = lock.writeLock();
        }
        write.lock();
    }
    public synchronized void unlock(String email){
        Lock write;
        if(filesLock.containsKey(email)){
            write = filesLock.get(email).writeLock();
        }else{
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
            filesLock.put(email,lock);
            write = lock.writeLock();
        }
        write.unlock();
    }

    public synchronized void writeUserToFile(User user) {
        /**
         * try-with-resources: tu fai il try, dichiari la variabile dentro il try e
         * fa la close in automatico senza che tu debba farla nel codice
         */
        try (ObjectOutputStream objectOut = new ObjectOutputStream(new FileOutputStream(FILE_PATH_TO_USE + user.getUser().toLowerCase() + ".txt"))) {
            objectOut.writeObject(user);
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            unlock(user.getUser());
        }

    }
}
