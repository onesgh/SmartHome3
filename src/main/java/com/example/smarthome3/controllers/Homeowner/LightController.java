package com.example.smarthome3.controllers.Homeowner;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;
import java.sql.Connection;
import java.net.URL;
import com.example.smarthome3.Database.DatabaseConnector;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        connection = DatabaseConnector.getConnection();
        updateDateTime();
        if (lightChart != null && lightXAxis != null && lightYAxis != null) {
            lightXAxis.setLabel("Time (Hours)");
            lightYAxis.setLabel("Light (Lux)");
            lightYAxis.setAutoRanging(true);
            loadLightData();
        }
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        String daySuffix = getDayOfMonthSuffix(now.getDayOfMonth());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a | EEEE, d 'of' MMMM, yyyy");
        String formattedDate = now.format(formatter).replaceFirst(
                "\\b" + now.getDayOfMonth() + "\\b",
                now.getDayOfMonth() + daySuffix
        );
        dateTimeLabel.setText(formattedDate);
        user_name.setText("Hi, " + com.example.smarthome3.Models.Model.getInstance().getViewFactory().getLoggedInUser() + " 👋");
    }

    private String getDayOfMonthSuffix(int day) {
        return getString(day);
    }

    static String getString(int day) {
        if (day >= 11 && day <= 13) return "th";
        return switch (day % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }

    private void loadLightData() {
        if (connection == null) return;
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Light Intensity");

        String query = "SELECT recorded_at, light_lux FROM Sensor WHERE light_lux IS NOT NULL ORDER BY recorded_at";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            light_listview.getItems().clear();
            int count = 0;
            while (rs.next()) {
                if (count++ % 3 != 0) continue;

                String timestamp = rs.getString("recorded_at");
                LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));

                double lux = rs.getDouble("light_lux");
                series.getData().add(new XYChart.Data<>(formattedTime, lux));

                light_listview.getItems().add(formattedTime + ": " + String.format("%.2f", lux) + " lx");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        lightChart.getData().clear();
        lightChart.getData().add(series);
        lightXAxis.setTickLabelRotation(45);
        lightChart.layout();
    }

    }
