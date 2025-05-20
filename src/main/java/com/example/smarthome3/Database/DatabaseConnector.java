package com.example.smarthome3.Database;

import java.sql.*;

public class DatabaseConnector {

    // Method to establish and return a database connection
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection with the database
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/Smart_Home2", "root", "MyTeam"
            );
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        return connection;
    }

    public static void main(String[] args) {
        Connection myConnection = null;

        try {
            // Get database connection
            myConnection = getConnection();
            if (myConnection == null) {
                System.out.println("Failed to establish a database connection.");
                return;
            }
            System.out.println("Database connection established successfully.");

        } finally {
            // Ensure connection is closed properly
            if (myConnection != null) {
                try {
                    myConnection.close();
                    System.out.println("Database connection closed.");
                } catch (SQLException e) {
                    System.out.println("Failed to close database connection: " + e.getMessage());
                }
            }
        }
    }
}
