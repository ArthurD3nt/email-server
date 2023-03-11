module com.example.servermail {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    exports com.example.servermail;
    exports com.example.bean;
    opens com.example.servermail to javafx.fxml;
    opens com.example.bean to javafx.fxml;
}