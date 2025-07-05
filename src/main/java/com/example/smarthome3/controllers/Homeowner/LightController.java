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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.*;
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
    public LineChart<String, Number> lightChart;
    @FXML
    public Text maxLightId;
    @FXML
    public Text minLightId;

    @FXML
    public Slider brightnessSlider;
    @FXML
    public Text BrightnessId;
    @FXML
    public Text AutoStatusId;

    private Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("✅ Light Dashboard Loaded");

        try {
            connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        updateDateTime();
        loadLightData();
        setupSliderListener();
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

        User currentUser = UserSession.getInstance().getCurrentUser(); // Changed from getUser()
        if (currentUser != null) {
            user_name.setText("Hi, " + currentUser.getName() + " 👋");
        } else {
            user_name.setText("Hi, Guest 👋"); // Fallback if no user is logged in
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

    private void loadLightData() {
        if (connection == null) return;

        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser == null) return;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Light Intensity");

        String query = """
        SELECT s.recorded_at, s.light_lux
        FROM Sensor s
        JOIN Home h ON s.HomeId = h.HomeId
        JOIN User u ON h.OwnerId = u.UserId
        WHERE u.UserId = ? AND s.light_lux IS NOT NULL
        ORDER BY s.recorded_at
    """;

        try (PreparedStatement stmt = connection.prepareStatement(
                query,
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY)) {
            stmt.setInt(1, currentUser.getID());
            stmt.setFetchSize(100);

            try (ResultSet rs = stmt.executeQuery()) {
                light_listview.getItems().clear();
                double max = Double.MIN_VALUE;
                double min = Double.MAX_VALUE;
                double last = 0;
                boolean hasData = false;

                while (rs.next()) {
                    hasData = true;
                    String timestamp = rs.getString("recorded_at");
                    LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));

                    double lux = rs.getDouble("light_lux");
                    last = lux;
                    max = Math.max(max, lux);
                    min = Math.min(min, lux);

                    series.getData().add(new XYChart.Data<>(formattedTime, lux));
                    light_listview.getItems().add(formattedTime + ": " + Math.round(lux) + " lx");
                }

                if (!hasData) {
                    maxLightId.setText("N/A");
                    minLightId.setText("N/A");

                } else {
                    maxLightId.setText(Math.round(max) + " lx");
                    minLightId.setText(Math.round(min) + " lx");

                }

                lightChart.getData().clear();
                lightChart.getData().add(series);
                lightXAxis.setTickLabelRotation(45);
                lightChart.layout();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load light data: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupSliderListener() {
        if (brightnessSlider != null && BrightnessId != null) {
            brightnessSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                BrightnessId.setText(Math.round(newVal.doubleValue()) + " %");
            });
        }
    }

}