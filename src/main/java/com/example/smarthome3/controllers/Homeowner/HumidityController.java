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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HumidityController implements Initializable {

    @FXML private ListView<String> humidity_listview;
    @FXML private Label dateTimeLabel;
    @FXML private Text user_name;
    @FXML private Text avgHumidityText;
    @FXML private Text maxHumidityText;
    @FXML private LineChart<String, Number> humidityChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            connection = DatabaseConnector.getConnection();
            updateDateTime();
            loadUserName();
            loadHumidityStats();
            loadHumidityData();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize DB: " + e.getMessage());
        }
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        String daySuffix = getDayOfMonthSuffix(now.getDayOfMonth());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a | EEEE, d 'of' MMMM, yyyy");
        String formatted = now.format(formatter).replaceFirst(
                "\\b" + now.getDayOfMonth() + "\\b",
                now.getDayOfMonth() + daySuffix
        );
        dateTimeLabel.setText(formatted);
    }

    private void loadUserName() {
        User currentUser = UserSession.getInstance().getCurrentUser(); // Changed from getUser()
        if (currentUser != null) {
            user_name.setText("Hi, " + currentUser.getName() + " 👋");
        } else {
            user_name.setText("Hi, Guest 👋"); // Fallback if no user is logged in
            System.err.println("❗ UserSession.getCurrentUser() returned null.");
        }
    }

    private void loadHumidityStats() {
        User currentUser = UserSession.getInstance().getCurrentUser(); // Changed from getUser()
        if (currentUser == null) return;

        String query = """
            SELECT AVG(s.humidity) AS avg_humidity, MAX(s.humidity) AS max_humidity
            FROM Sensor s
            JOIN Home h ON s.HomeId = h.HomeId
            WHERE h.OwnerId = ? AND s.humidity IS NOT NULL
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, currentUser.getID());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    avgHumidityText.setText(String.format("%.1f%%", rs.getDouble("avg_humidity")));
                    maxHumidityText.setText(String.format("%.1f%%", rs.getDouble("max_humidity")));
                } else {
                    avgHumidityText.setText("N/A");
                    maxHumidityText.setText("N/A");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadHumidityData() {
        if (connection == null) return;

        User currentUser = UserSession.getInstance().getCurrentUser(); // Changed from getUser()
        if (currentUser == null) return;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Humidity");
        humidity_listview.getItems().clear();

        String query = """
            SELECT s.recorded_at, s.humidity
            FROM Sensor s
            JOIN Home h ON s.HomeId = h.HomeId
            WHERE h.OwnerId = ?
            ORDER BY s.recorded_at
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, currentUser.getID());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String timestamp = rs.getString("recorded_at");
                    double humidity = rs.getDouble("humidity");

                    LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    String formatted = dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));

                    // Add each point to chart and list
                    series.getData().add(new XYChart.Data<>(formatted, humidity));
                    humidity_listview.getItems().add(String.format("%s: %.0f%%", formatted, humidity));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        humidityChart.getData().clear();
        humidityChart.getData().add(series);
        xAxis.setTickLabelRotation(45);
        humidityChart.layout();
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

    @Override
    protected void finalize() throws Throwable {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("✅ Database connection closed in HumidityController.");
        }
        super.finalize();
    }
}