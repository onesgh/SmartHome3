package com.example.smarthome3.controllers.Homeowner;

import com.example.smarthome3.Database.DatabaseConnector;
import com.example.smarthome3.Models.Model;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HomeownerController implements Initializable {

    @FXML
    private BorderPane homeowner_parent;
    @FXML
    private Text user_name;

    @FXML
    private LineChart<String, Number> humidityChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    @FXML
    private LineChart<String, Number> TemperatureChart;
    @FXML
    private CategoryAxis xAxis1;
    @FXML
    private NumberAxis yAxis1;

    @FXML
    private LineChart<String, Number> lightChart;
    @FXML
    private CategoryAxis lightXAxis;
    @FXML
    private NumberAxis lightYAxis;

    @FXML
    public ListView<String> humidity_listview;
    @FXML
    public ListView<String> temperature_listview;
    @FXML
    public ListView<String> light_listview;

    @FXML
    private Label dateTimeLabel;

    private Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initializing HomeownerController...");

        String name = Model.getInstance().getViewFactory().getLoggedInUser();
        if (user_name != null && name != null) {
            user_name.setText("Hi, " + name + " 👋");
        }

        connection = DatabaseConnector.getConnection();

        if (dateTimeLabel != null) {
            LocalDateTime now = LocalDateTime.now();

            String daySuffix = getDayOfMonthSuffix(now.getDayOfMonth());
            DateTimeFormatter baseFormatter = DateTimeFormatter.ofPattern("h:mm a | EEEE, d 'of' MMMM, yyyy");
            String formattedDate = now.format(baseFormatter);

            int day = now.getDayOfMonth();
            formattedDate = formattedDate.replaceFirst("\\b" + day + "\\b", day + daySuffix);

            dateTimeLabel.setText(formattedDate);
        }

        // Humidity Chart setup
        if (xAxis != null && yAxis != null && humidityChart != null) {
            xAxis.setLabel("Time (Days)");
            yAxis.setLabel("Humidity (%)");
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(100);
            loadHumidityData();
        }

        // Temperature Chart setup
        if (xAxis1 != null && yAxis1 != null && TemperatureChart != null) {
            xAxis1.setLabel("Time (Days)");
            yAxis1.setLabel("Temperature (°C)");
            yAxis1.setAutoRanging(true);
            loadTemperatureData();
        }

        // Light Chart setup
        if (lightXAxis != null && lightYAxis != null && lightChart != null) {
            lightXAxis.setLabel("Time (Hours)");
            lightYAxis.setLabel("Light Lux");
            lightYAxis.setAutoRanging(true);
            loadLightData();
        }

        // Menu Switching Listener
        Model.getInstance().getViewFactory().getHomeownerSelectedMenuItem().addListener((obs, oldVal, newVal) -> {
            Node view = switch (newVal) {
                case "Humidity" -> Model.getInstance().getViewFactory().getHumidityView();
                case "Temperature" -> Model.getInstance().getViewFactory().getTemperatureView();
                case "Motion" -> Model.getInstance().getViewFactory().getMotionView();
                case "Light" -> {
                    try {
                        yield Model.getInstance().getViewFactory().getLightView();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                default -> Model.getInstance().getViewFactory().getDashboardView();
            };

            if (homeowner_parent != null && view != null) {
                homeowner_parent.setCenter(view);
            }
        });
    }

    // Helper method to get suffix for day number (st, nd, rd, th)
    private String getDayOfMonthSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    private void loadTemperatureData() {
        if (connection == null) return;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Temperature");

        String query = "SELECT recorded_at, temperature FROM sensor_data WHERE temperature IS NOT NULL ORDER BY recorded_at";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (temperature_listview != null) temperature_listview.getItems().clear();

            int count = 0;
            while (rs.next()) {
                if (count++ % 3 != 0) continue; // Skip 2 of every 3 points

                String timestamp = rs.getString("recorded_at");
                LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));

                double temp = rs.getDouble("temperature");
                series.getData().add(new XYChart.Data<>(formattedTime, temp));

                if (temperature_listview != null)
                    temperature_listview.getItems().add(formattedTime + ": " + String.format("%.2f", temp) + "°C");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        TemperatureChart.getData().clear();
        TemperatureChart.getData().add(series);
        xAxis1.setTickLabelRotation(45);
        TemperatureChart.layout();
    }

    private void loadHumidityData() {
        if (connection == null) return;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Humidity");

        String query = "SELECT recorded_at, humidity FROM sensor_data WHERE humidity IS NOT NULL ORDER BY recorded_at";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (humidity_listview != null) humidity_listview.getItems().clear();

            int count = 0;
            while (rs.next()) {
                if (count++ % 3 != 0) continue; // Optional: skip 2 of every 3 data points

                String timestamp = rs.getString("recorded_at");
                LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));

                double humidity = rs.getDouble("humidity");
                series.getData().add(new XYChart.Data<>(formattedTime, humidity));

                if (humidity_listview != null)
                    humidity_listview.getItems().add(formattedTime + ": " + String.format("%.2f", humidity) + "%");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        humidityChart.getData().clear();
        humidityChart.getData().add(series);
        xAxis.setTickLabelRotation(45); // Rotate x-axis labels
        humidityChart.layout();
    }

    private void loadLightData() {
        if (connection == null) return;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Light_Lux");

        String query = "SELECT recorded_at, light_lux FROM sensor_data WHERE light_lux IS NOT NULL ORDER BY recorded_at";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (light_listview != null) light_listview.getItems().clear();

            int count = 0;
            while (rs.next()) {
                if (count++ % 3 != 0) continue;

                String timestamp = rs.getString("recorded_at");
                LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));

                double light = rs.getDouble("light_lux");
                series.getData().add(new XYChart.Data<>(formattedTime, light));

                if (light_listview != null)
                    light_listview.getItems().add(formattedTime + ": " + String.format("%.2f", light) + " lx");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        lightChart.getData().clear();
        lightChart.getData().add(series);
        lightXAxis.setTickLabelRotation(45);
        lightChart.layout();
    }
}
