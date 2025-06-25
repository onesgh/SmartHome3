package com.example.smarthome3.controllers.Homeowner;
import com.example.smarthome3.Database.DatabaseConnector;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
public class TemperatureController  implements Initializable {
    @FXML

    public Text user_name;
    @FXML
    public Label dateTimeLabel;
    @FXML
    public NumberAxis yAxis1;
    @FXML
    public CategoryAxis xAxis1;
    @FXML
    public LineChart TemperatureChart;
    @FXML
    public Slider temperatureSlider;
    private Connection connection;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connection = DatabaseConnector.getConnection();
        updateDateTime();
        loadTemperatureData();
    }
    private void loadTemperatureData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Temperature");

        String query = "SELECT recorded_at, temperature FROM Sensor WHERE temperature IS NOT NULL ORDER BY recorded_at";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            int count = 0;
            while (rs.next()) {
                if (count++ % 3 != 0) continue;

                String timestamp = rs.getString("recorded_at");
                LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
                double temperature = rs.getDouble("temperature");

                series.getData().add(new XYChart.Data<>(formattedTime, temperature));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        TemperatureChart.getData().clear();
        TemperatureChart.getData().add(series);
        xAxis1.setTickLabelRotation(45);
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
