package com.example.smarthome3.Database;

import jdk.internal.misc.ThreadFlock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {
    // Configuration constants (ideally loaded from a config file or env variables)
    private static final String DB_URL = "jdbc:mariadb://195.235.211.197/pii2_SmartSphere";
    private static final String DB_USER = "pii2_SmartSphere";
    private static final String DB_PASSWORD = "Smarthome1";
    private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";

    // Method to establish and return a database connection
    public static Connection getConnection() throws SQLException {
        try {
            // Load MariaDB JDBC Driver
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MariaDB JDBC Driver not found: " + e.getMessage(), e);
        }

        // Establish connection
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        // Validate connection
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Failed to establish a valid database connection");
        }

        return connection;
    }
    public void closeConnection() {
        try {
            Connection connection = null;
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database connection closed successfully.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing database connection: " + e.getMessage());
        }
    }
    // Test method to verify connection (optional, consider moving to a test class)
    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("Database connection established successfully.");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }
}