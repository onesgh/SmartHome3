package com.example.smarthome3.controllers.Homeowner;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
public class MotionController implements Initializable  {
    @FXML
    public ListView<String> motion_listview;
    public Text user_name;
    public Label dateTimeLabel;
    public NumberAxis lightYAxis;
    public CategoryAxis lightXAxis;
    public LineChart lightChart;
    public Text lastMotionDetected;
    public CheckBox enableMotionSensors;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("✅ Motion Dashboard Loaded");
        if (motion_listview == null) {
            System.err.println("❌ Error: motion_listview is null. Check FXML file for fx:id='motion_listview'.");
            return;
        }
        // Example data (replace with real motion sensor data later)
        motion_listview.getItems().addAll("Living Room: No Motion", "Garage: Motion Detected", "Backyard: No Motion");
    }
}
