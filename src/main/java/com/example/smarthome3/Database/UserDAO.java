package com.example.smarthome3.Database;

import java.sql.*;

public class UserDAO {
    private final DatabaseConnector databaseConnector;

    public UserDAO(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    // Create a new user
    public boolean createUser(String username, String email, String passwordHash, String accountType) {
        String sql = "INSERT INTO User (username, email, passwordHash, accountType) VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, passwordHash); // Store the password as is (plain text)
            stmt.setString(4, accountType);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Read user by username (for login validation)
    public ResultSet getUserByUsername(String username) {
        String sql = "SELECT * FROM User WHERE username = ?";
        try {
            Connection conn = databaseConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Update user password (assuming user will provide username for password reset)
    public boolean updateUserPassword(String username, String newPasswordHash) {
        String sql = "UPDATE User SET passwordHash = ? WHERE username = ?";
        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPasswordHash); // Store the password as is (plain text)
            stmt.setString(2, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete user by username (for deleting user by username)
    public boolean deleteUser(String username) {
        String sql = "DELETE FROM User WHERE username = ?";
        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
