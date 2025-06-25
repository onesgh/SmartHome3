package com.example.smarthome3.controllers.Homeowner;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class LightController implements Initializable {
    @FXML
    public ListView<String> light_listview;
    @FXML
    public Text user_name;
    @FXML
    public Label dateTimeLabel;
    @FXML
    public NumberAxis lightYAxis;
    @FXML
    public CategoryAxis lightXAxis;
    @FXML
    public LineChart lightChart;
    @FXML
    public Slider brightnessSlider;
    private Connection connection;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("✅ Light Dashboard Loaded");
        if (light_listview == null) {
            System.err.println("❌ Error: light_listview is null. Check FXML file for fx:id='light_listview'.");
            return;
        }
        // Example data (replace with real light sensor data later)
        light_listview.getItems().addAll("Living Room: ON", "Kitchen: OFF", "Garage: ON");
    }
}