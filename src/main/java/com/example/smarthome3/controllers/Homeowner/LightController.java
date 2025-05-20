package com.example.smarthome3.controllers.Homeowner;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;
public class LightController implements Initializable {
    @FXML
    public ListView<String> light_listview;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("✅ Light Dashboard Loaded");
        // Example data (replace with real light sensor data later)
        light_listview.getItems().addAll("Living Room: ON", "Kitchen: OFF", "Garage: ON");
    }
}
