package com.example.server.servermail;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.server.model.LogModel;

import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;

public class LogController {
    

    @FXML
    public TextFlow logFlow;
    private LogModel logModel;

    public LogController(){
        logModel = new LogModel();
        logModel.getLog().addListener((observable, oldValue, newValue) -> {
            setLog(newValue);
        });

        Runnable serverThread = new Server(logModel);
        new Thread(serverThread).start();

    }

    public void initialize() {
        logModel.setLog("Server start");
    }

    public void setLog(String log) {
            Text fullLog= new Text("["+LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +"]\t -- " + "  " + log+"\n");
            fullLog.setFill(Color.web("#d4d4d4"));
            logFlow.getChildren().add(fullLog);
    }

}
