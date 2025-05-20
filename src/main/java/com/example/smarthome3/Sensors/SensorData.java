package com.example.smarthome3.Sensors;

public class SensorData {
    public float temperature;
    public float humidity;
    public float lightLux;

    public SensorData(float temp, float hum, float lux) {
        this.temperature = temp;
        this.humidity = hum;
        this.lightLux = lux;
    }
}
