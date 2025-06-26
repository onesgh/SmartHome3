package com.example.smarthome3.controllers.Homeowner;

import com.example.smarthome3.Database.DatabaseConnector;
import com.example.smarthome3.Models.Model;
import com.example.smarthome3.Database.UserSession;
import com.example.smarthome3.Database.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TemperatureController implements Initializable {

    @FXML public Text user_name;
    @FXML public Label dateTimeLabel;
    @FXML public NumberAxis yAxis1;
    @FXML public CategoryAxis xAxis1;
    @FXML public LineChart<String, Number> TemperatureChart;
    @FXML public Slider temperatureSlider;
    @FXML public Text CurrentTemperatureId;
    @FXML public Text HighestTempId;
    @FXML public Text LowestTempId;
    @FXML public Text AdjustTempId;

    private Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }

        updateDateTime();
        loadTemperatureData();
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

        String username = Model.getInstance().getViewFactory().getLoggedInUser();
        user_name.setText("Hi, " + username + " 👋");
    }

    private void loadTemperatureData() {
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) {
            System.err.println("No user is logged in.");
            return;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Temperature");

        String query = """
            SELECT s.temperature, s.recorded_at
            FROM Sensor s
            JOIN Home h ON s.HomeId = h.HomeId
            WHERE h.OwnerId = ?
            ORDER BY s.recorded_at
        """;

        double highest = Double.MIN_VALUE;
        double lowest = Double.MAX_VALUE;
        double currentTemp = 0;
        int count = 0;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, currentUser.getID());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double temperature = rs.getDouble("temperature");
                String timestamp = rs.getString("recorded_at");

                LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));

                if (count++ % 3 == 0) {
                    series.getData().add(new XYChart.Data<>(formattedTime, temperature));
                }

                // Keep track of values
                highest = Math.max(highest, temperature);
                lowest = Math.min(lowest, temperature);
                currentTemp = temperature; // last one will be considered current
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // Show chart
        TemperatureChart.getData().clear();
        TemperatureChart.getData().add(series);
        xAxis1.setTickLabelRotation(45);

        // Update UI stats
        CurrentTemperatureId.setText((int) currentTemp + "°C");
        HighestTempId.setText((int) highest + "°C");
        LowestTempId.setText((int) lowest + "°C");
        temperatureSlider.setValue(currentTemp);
        AdjustTempId.setText((int) temperatureSlider.getValue() + "°C");

        // Optional: update AdjustTempId on slider move
        temperatureSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                AdjustTempId.setText(newVal.intValue() + "°C")
        );
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
