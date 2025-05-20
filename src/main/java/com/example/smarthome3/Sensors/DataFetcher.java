package com.example.smarthome3.Sensors;
import java.sql.*;

public class DataFetcher {
    public SensorData fetchLatest() {
        String url = "jdbc:mysql://localhost:3306/Smart_Home2";
        String user = "root";
        String password = "MyTeam";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT temperature, humidity, light_lux FROM sensor_data ORDER BY recorded_at DESC LIMIT 1")) {

            if (rs.next()) {
                return new SensorData(
                        rs.getFloat("temperature"),
                        rs.getFloat("humidity"),
                        rs.getFloat("light_lux")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
