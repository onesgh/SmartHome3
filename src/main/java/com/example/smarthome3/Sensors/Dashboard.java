package com.example.smarthome3.Sensors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Dashboard extends Application {

    XYChart.Series<Number, Number> temperatureSeries = new XYChart.Series<>();
    XYChart.Series<Number, Number> humiditySeries = new XYChart.Series<>();
    XYChart.Series<Number, Number> lightSeries = new XYChart.Series<>();

    int time = 0;

    @Override
    public void start(Stage primaryStage) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (s)");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Sensor Values");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Real-Time Sensor Readings");

        temperatureSeries.setName("Temperature (°C)");
        humiditySeries.setName("Humidity (%)");
        lightSeries.setName("Light (Lux)");

        chart.getData().addAll(temperatureSeries, humiditySeries, lightSeries);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            SensorData data = new DataFetcher().fetchLatest();
            if (data != null) {
                temperatureSeries.getData().add(new XYChart.Data<>(time, data.temperature));
                humiditySeries.getData().add(new XYChart.Data<>(time, data.humidity));
                lightSeries.getData().add(new XYChart.Data<>(time, data.lightLux));
                time += 2;
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        Scene scene = new Scene(chart, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Smart Home Sensor Dashboard");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


