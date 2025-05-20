package com.example.smarthome3.controllers.Homeowner;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;
public class TemperatureController  implements Initializable{
    @FXML
    public ListView<String> temperature_listview;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("✅ Temperature Dashboard Loaded");
        // Example data (replace with real data later)
        temperature_listview.getItems().addAll("Living Room: 22°C", "Bedroom: 24°C", "Kitchen: 23°C");
    }
}
