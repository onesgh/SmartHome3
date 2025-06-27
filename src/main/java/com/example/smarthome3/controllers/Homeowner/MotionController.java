package com.example.smarthome3.controllers.Homeowner;

import com.example.smarthome3.Database.DatabaseConnector;

import com.example.smarthome3.Database.User;
import com.example.smarthome3.Database.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MotionController implements Initializable {

    @FXML public Text user_name;
    @FXML public Label dateTimeLabel;
    @FXML public LineChart<String, Number> motionChart;
    @FXML public Text lastMotionDetectedId;
    @FXML public CheckBox enableMotionSensors;
    @FXML public CategoryAxis MotionXAxis;
    @FXML public NumberAxis MotionYAxis;
    @FXML public Text AlertsTId;
    @FXML public Text ActiveSensorId;

    private Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("DB connection error", e);
        }

        updateDateTime();
        loadMotionData();
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

        User currentUser = UserSession.getInstance().getUser();
        if (currentUser != null) {
            user_name.setText("Hi, " + currentUser.getName() + " 👋");
        }
    }

    private void loadMotionData() {
        User user = UserSession.getInstance().getUser();
        if (user == null) {
            System.err.println("User is not logged in.");
            return;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Motion");

        String motionQuery = """
            SELECT s.recorded_at, s.motion
            FROM Sensor s
            JOIN Home h ON s.HomeId = h.HomeId
            WHERE s.motion IS NOT NULL AND h.OwnerId = ?
            ORDER BY s.recorded_at
        """;

        int totalMotions = 0;
        int todayAlerts = 0;
        String lastMotion = "N/A";

        try (PreparedStatement stmt = connection.prepareStatement(motionQuery)) {
            stmt.setInt(1, user.getID());
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                String timestamp = rs.getString("recorded_at");
                int motion = rs.getInt("motion");

                if (motion > 0) {
                    totalMotions++;
                    LocalDate motionDate = LocalDate.parse(timestamp.substring(0, 10));
                    if (motionDate.equals(LocalDate.now())) {
                        todayAlerts++;
                    }
                    lastMotion = timestamp;
                }

                if (count++ % 3 == 0) {
                    LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
                    series.getData().add(new XYChart.Data<>(formattedTime, motion));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Update chart
        motionChart.getData().clear();
        motionChart.getData().add(series);
        MotionXAxis.setTickLabelRotation(45);

        // Update labels
        ActiveSensorId.setText(String.valueOf(totalMotions));
        AlertsTId.setText(String.valueOf(todayAlerts));
        lastMotionDetectedId.setText(lastMotion);

        // Optionally disable the checkbox since it's not backed by data anymore
        enableMotionSensors.setDisable(true);
        enableMotionSensors.setSelected(false);
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
