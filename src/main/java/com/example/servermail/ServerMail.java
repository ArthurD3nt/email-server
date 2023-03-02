package com.example.servermail;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMail extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Runnable serverThread = new Server();
        new Thread(serverThread).start();

        FXMLLoader fxmlLoader = new FXMLLoader(ServerMail.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 300);
        stage.setTitle("Server log");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }

}