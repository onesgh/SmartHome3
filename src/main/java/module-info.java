module com.example.smarthome3{
    requires javafx.controls;
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.sql;
    requires com.sun.jna.platform;


    opens com.example.smarthome3 to javafx.fxml;
    opens com.example.smarthome3.controllers to javafx.fxml;
    opens com.example.smarthome3.controllers.SecurityGuard to javafx.fxml;
    opens com.example.smarthome3.controllers.Technician to javafx.fxml;
    opens com.example.smarthome3.controllers.Homeowner to javafx.fxml;
    exports com.example.smarthome3;
    exports com.example.smarthome3.controllers;
    exports com.example.smarthome3.controllers.Homeowner;
    exports com.example.smarthome3.controllers.SecurityGuard;
    exports com.example.smarthome3.controllers.Technician;
    exports com.example.smarthome3.Models;
    exports com.example.smarthome3.Views;
}