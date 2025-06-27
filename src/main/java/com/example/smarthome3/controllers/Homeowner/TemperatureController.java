package com.example.smarthome3.controllers.Homeowner;

import com.example.smarthome3.Database.DatabaseConnector;
import com.example.smarthome3.Database.UserSession;
import com.example.smarthome3.Database.User;
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

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TemperatureController implements Initializable {
    @FXML public ListView<String> Temperature_listview;
    @FXML public Text user_name;
    @FXML public Label dateTimeLabel;
    @FXML public NumberAxis yAxis1;
    @FXML public CategoryAxis xAxis1;
    @FXML public LineChart<String, Number> TemperatureChart;
    @FXML public Text HighestTempId;
    @FXML public Text LowestTempId;
    @FXML public Text CurrentTemperatureId;
    @FXML public Text AdjustTempId;
    @FXML public Slider temperatureSlider;

    private Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        updateDateTime();
        loadTemperatureData();
        temperatureSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            AdjustTempId.setText(String.format("%.0f°C", newVal.doubleValue()));
        });
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy - HH:mm");
        dateTimeLabel.setText(now.format(formatter));

        User currentUser = UserSession.getInstance().getUser();
        if (currentUser != null) {
            user_name.setText("Hi, " + currentUser.getName() + " 👋");
        }
    }

    private void loadTemperatureData() {
        if (connection == null) return;

        User currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) return;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Temperature (°C)");

        String query = """
            SELECT s.recorded_at, s.temperature
            FROM Sensor s
            JOIN Home h ON s.HomeId = h.HomeId
            WHERE h.OwnerId = ?
            ORDER BY s.recorded_at
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, currentUser.getID());
            ResultSet rs = stmt.executeQuery();
            Temperature_listview.getItems().clear();
            double max = Double.MIN_VALUE;
            double min = Double.MAX_VALUE;
            double last = 0;

            while (rs.next()) {
                String timestamp = rs.getString("recorded_at");
                double temp = rs.getDouble("temperature");
                last = temp;
                max = Math.max(max, temp);
                min = Math.min(min, temp);

                LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
                series.getData().add(new XYChart.Data<>(formattedTime, temp));
            }

            HighestTempId.setText(String.format("%.0f °C", max));
            LowestTempId.setText(String.format("%.0f °C", min));
            CurrentTemperatureId.setText(String.format("%.0f °C", last));
            AdjustTempId.setText(String.format("%.0f °C", last));

            TemperatureChart.getData().clear();
            TemperatureChart.getData().add(series);
            xAxis1.setTickLabelRotation(45);
            TemperatureChart.layout();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
