module com.example.servermail {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    exports com.example.server.servermail;
    exports com.example.transmission;
    opens com.example.server.servermail to javafx.fxml;
    opens com.example.transmission to javafx.fxml;
}