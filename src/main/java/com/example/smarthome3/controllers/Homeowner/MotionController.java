package com.example.smarthome3.controllers.Homeowner;
import com.example.smarthome3.Database.DatabaseConnector;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
public class MotionController implements Initializable  {

    public Text user_name;
    @FXML
    public Label dateTimeLabel;
    @FXML
    public LineChart motionChart;
    @FXML
    public Text lastMotionDetected;
    @FXML
    public CheckBox enableMotionSensors;
    @FXML
    public CategoryAxis MotionXAxis;
    @FXML
    public NumberAxis MotionYAxis;
    public Text lastMotionDetectedId;
    public Text AlertsTId;
    public Text ActiveSensorId;

    private Connection connection;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        updateDateTime();
        loadMotionData();
    }

    private void loadMotionData() { XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Motion");

        String query = "SELECT recorded_at, motion FROM Sensor WHERE motion IS NOT NULL ORDER BY recorded_at";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            int count = 0;
            while (rs.next()) {
                if (count++ % 3 != 0) continue;
                String timestamp = rs.getString("recorded_at");
                LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
                int motionValue = rs.getInt("motion"); // assuming motion is stored as 0/1
                series.getData().add(new XYChart.Data<>(formattedTime, motionValue));
            }

        } catch (SQLException e) {
            e.printStackTrace();
    }
        motionChart.getData().clear();
        motionChart.getData().add(series);
        MotionXAxis.setTickLabelRotation(45);
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
        if (day >= 11 && day <= 13) return "th";
        return switch (day % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }
    }
