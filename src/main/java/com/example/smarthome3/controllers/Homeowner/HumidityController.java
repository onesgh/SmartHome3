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
    @FXML
    public ListView<String> humidity_listview;
    @FXML private Label dateTimeLabel;
    @FXML private Text user_name;
    @FXML private LineChart<String, Number> humidityChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private Connection connection;

    @Override
    public void initialize(URL url , ResourceBundle resourceBundle) {
        connection = DatabaseConnector.getConnection();
        updateDateTime();
        loadHumidityData();
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
    private void loadHumidityData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Humidity");

        String query = "SELECT recorded_at, humidity FROM Sensor WHERE humidity IS NOT NULL ORDER BY recorded_at";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            int count = 0;
            while (rs.next()) {
                if (count++ % 3 != 0) continue;
                String timestamp = rs.getString("recorded_at");
                LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
                double humidity = rs.getDouble("humidity");
                series.getData().add(new XYChart.Data<>(formattedTime, humidity));
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
            default -> "th";
        };
    }
    }



