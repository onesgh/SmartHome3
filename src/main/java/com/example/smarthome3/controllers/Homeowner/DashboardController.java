package com.example.smarthome3.controllers.Homeowner;

import com.example.smarthome3.Database.DatabaseConnector;
import com.example.smarthome3.Database.User;
import com.example.smarthome3.Database.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Text user_name;
    @FXML
    private Label dateTime;


    @FXML
    private Text lightValueText;
    @FXML
    private Text temperatureValueText;
    @FXML
    private Text humidityValueText;
    @FXML
    private Text motionValueText;

    private Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("✅ Dashboard Loaded");

        try {
            connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        updateDateTime();
        loadSensorData();
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        String suffix = getDayOfMonthSuffix(now.getDayOfMonth());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a | EEEE, d 'of' MMMM, yyyy");
        String formattedDate = now.format(formatter).replaceFirst(
                "\\b" + now.getDayOfMonth() + "\\b",
                now.getDayOfMonth() + suffix
        );
        dateTime.setText(formattedDate);

        User user = UserSession.getInstance().getCurrentUser();
        if (user != null) {
            user_name.setText("Hi, " + user.getName() + " 👋");
        } else {
            user_name.setText("Hi, Guest 👋");
        }
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

    private void loadSensorData() {
        if (connection == null) return;

        User user = UserSession.getInstance().getCurrentUser();
        if (user == null) return;

        String query = """
            SELECT s.light_lux, s.temperature, s.humidity, s.motion
            FROM Sensor s
            JOIN Home h ON s.HomeId = h.HomeId
            WHERE h.OwnerId = ?
            ORDER BY s.recorded_at DESC
            LIMIT 1
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, user.getID());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int lux = rs.getInt("light_lux");
                    int temp = rs.getInt("temperature");
                    int humidity = rs.getInt("humidity");
                    int motion = rs.getInt("motion");

                    lightValueText.setText(lux + " lux");
                    temperatureValueText.setText(temp + "°C | Auto Room Temperature");
                    humidityValueText.setText("Humidity: " + humidity + "%");
                    motionValueText.setText(motion + " Connected Devices");
                } else {
                    lightValueText.setText("N/A");
                    temperatureValueText.setText("N/A");
                    humidityValueText.setText("N/A");
                    motionValueText.setText("N/A");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}