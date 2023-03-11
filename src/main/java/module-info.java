module com.example.servermail {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.servermail to javafx.fxml;
    exports com.example.servermail;
    exports com.example.bean;
    opens com.example.bean to javafx.fxml;
}