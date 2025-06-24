package com.example.smarthome3.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    // Method to establish and return a database connection
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Remote DB connection URL, username, password
            String url = "jdbc:mysql://195.235.211.197:3306/pii2_SmartSphere";  // your remote DB URL
            String user = "pii2_SmartSphere";                      // your remote DB username
            String password = "Smarthome1";                  // your remote DB password

            // Establish connection with the remote database
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        return connection;
    }

    public static void main(String[] args) {
        try (Connection myConnection = getConnection()) {
            if (myConnection != null) {
                System.out.println("Database connection established successfully.");
            } else {
                System.out.println("Failed to establish a database connection.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to close database connection: " + e.getMessage());
        }
    }
}