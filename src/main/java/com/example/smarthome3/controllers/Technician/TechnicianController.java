package com.example.smarthome3.controllers.Technician;

import com.example.smarthome3.Database.DatabaseConnector;
import com.example.smarthome3.Database.User;
import com.example.smarthome3.Database.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class TechnicianController implements Initializable {

    @FXML private ListView<String> request_listview;
    @FXML private Button resolve_btn;
    @FXML private Button reject_btn;
    @FXML private Button refresh_btn;
    @FXML private Button Logout_btn1;

    private Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
        }

        User currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) {
            showAlert("Error", "No user session found.");
            return;
        }

        loadRequests(currentUser.getID());

        resolve_btn.setOnAction(event -> handleRequestAction(currentUser.getID(), "resolved"));
        reject_btn.setOnAction(event -> handleRequestAction(currentUser.getID(), "rejected"));
        refresh_btn.setOnAction(event -> loadRequests(currentUser.getID()));

        if (Logout_btn1 != null) {
            Logout_btn1.setOnAction(event -> logoutAndRedirectToLogin());
        } else {
            System.out.println("❌ Logout button is not linked!");
        }
    }

    private void loadRequests(int userId) {
        request_listview.getItems().clear();

        String query = """
            SELECT Timestamp, Message
            FROM Alert
            WHERE UserId = ?
            ORDER BY Timestamp DESC
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("Timestamp");
                String message = rs.getString("Message");
                request_listview.getItems().add(timestamp + " - " + message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load requests.");
        }
    }

    private void handleRequestAction(int userId, String status) {
        String selected = request_listview.getSelectionModel().getSelectedItem();
        if (selected != null) {
            insertLog(userId, "Request " + status + ": " + selected);
            request_listview.getItems().remove(selected);
        }
    }

    private void insertLog(int userId, String action) {
        String getHomeQuery = "SELECT HomeId FROM Home WHERE TechnicianId = ?";
        String insertQuery = "INSERT INTO Log (Timestamp, Action, UserId, HomeId) VALUES (?, ?, ?, ?)";

        try (PreparedStatement homeStmt = connection.prepareStatement(getHomeQuery)) {
            homeStmt.setInt(1, userId);
            ResultSet rs = homeStmt.executeQuery();
            if (rs.next()) {
                int homeId = rs.getInt("HomeId");
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    insertStmt.setString(2, action);
                    insertStmt.setInt(3, userId);
                    insertStmt.setInt(4, homeId);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void logoutAndRedirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Login");
            stage.show();

            Stage currentStage = (Stage) Logout_btn1.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
