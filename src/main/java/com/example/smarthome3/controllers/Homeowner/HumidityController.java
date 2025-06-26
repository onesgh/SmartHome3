package com.example.smarthome3.controllers.Homeowner;

import com.example.smarthome3.Database.DatabaseConnector;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            loadHumidityData();
            loadHumidityStats();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection: " + e.getMessage());
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
    }

    private void loadUserName() {
        // Replace with actual logic to get logged-in user ID (e.g., from session or model)
        String loggedInUserId = "1"; // Example: Replace with actual user ID retrieval
        String query = "SELECT username FROM User WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, loggedInUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user_name.setText("Hi, " + rs.getString("username") + " 👋");
                } else {
                    user_name.setText("Hi, Guest 👋");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            user_name.setText("Hi, Error 👋");
        }
    }

    private void loadHumidityStats() {
        String query = "SELECT AVG(humidity) as avg_humidity, MAX(humidity) as max_humidity FROM Sensor WHERE humidity IS NOT NULL";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                double avgHumidity = rs.getDouble("avg_humidity");
                double maxHumidity = rs.getDouble("max_humidity");
                avgHumidityText.setText(String.format("%.1f%%", avgHumidity));
                maxHumidityText.setText(String.format("%.1f%%", maxHumidity));
            } else {
                avgHumidityText.setText("N/A");
                maxHumidityText.setText("N/A");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            avgHumidityText.setText("Error");
            maxHumidityText.setText("Error");
        }
    }

    private void loadHumidityData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Humidity");
        humidity_listview.getItems().clear();

        String query = "SELECT recorded_at, humidity FROM Sensor WHERE humidity IS NOT NULL ORDER BY recorded_at";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                String timestamp = rs.getString("recorded_at");
                LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
                double humidity = rs.getDouble("humidity");

                // Add to chart (every 3rd record to avoid clutter)
                if (count++ % 3 == 0) {
                    series.getData().add(new XYChart.Data<>(formattedTime, humidity));
                }
                // Add to ListView
                humidity_listview.getItems().add(String.format("%s: %.1f%%", formattedTime, humidity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        humidityChart.getData().clear();
        humidityChart.getData().add(series);
        xAxis.setTickLabelRotation(45);
    }

    private String getDayOfMonthSuffix(int day) {
        if (day >= 11 && day <= 13) return "th";
        return switch (day % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th Juno";
        };
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.finalize();
    }
}