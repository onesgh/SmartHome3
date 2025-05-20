package com.example.smarthome3.controllers.Homeowner;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;
public class MotionController implements Initializable  {
    @FXML
    public ListView<String> motion_listview;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("✅ Motion Dashboard Loaded");
        // Example data (replace with real motion sensor data later)
        motion_listview.getItems().addAll("Living Room: No Motion", "Garage: Motion Detected", "Backyard: No Motion");
    }
}
