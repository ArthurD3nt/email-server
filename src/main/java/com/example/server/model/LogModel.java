package com.example.server.model;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;

public class LogModel {
    public SimpleStringProperty log;

    public LogModel() {
        log = new SimpleStringProperty("Start server \n");
    }
    
    public SimpleStringProperty getLog() {
        return log;
    }

    public void setLog(String newLine) {
        Platform.runLater(()-> log.set(newLine));

    }
}
