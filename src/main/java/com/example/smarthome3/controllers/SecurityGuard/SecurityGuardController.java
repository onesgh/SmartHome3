package com.example.smarthome3.controllers.SecurityGuard;

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

public class SecurityGuardController implements Initializable {

    @FXML private ListView<String> activity_log_listview;
    @FXML private TextField visitor_name_field;
    @FXML private TextField visitor_purpose_field;
    @FXML private TextArea incident_report_area;
    @FXML private Button checkin_btn;
    @FXML private Button checkout_btn;
    @FXML private Button report_incident_btn;
    @FXML private Button emergency_alert_btn;
    @FXML private Button refresh_btn;
    @FXML private Button logout_btn2;

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

        refreshActivityLog(currentUser.getID());

        checkin_btn.setOnAction(event -> checkInVisitor(currentUser.getID()));
        checkout_btn.setOnAction(event -> checkOutVisitor(currentUser.getID()));
        report_incident_btn.setOnAction(event -> reportIncident(currentUser.getID()));
        emergency_alert_btn.setOnAction(event -> triggerEmergencyAlert(currentUser.getID()));
        refresh_btn.setOnAction(event -> refreshActivityLog(currentUser.getID()));
        logout_btn2.setOnAction(event -> logoutAndRedirectToLogin());
    }

    private void refreshActivityLog(int userId) {
        activity_log_listview.getItems().clear();

        String query = """
           SELECT Timestamp, Action
                             FROM Log
                             WHERE UserId = ?
                             ORDER BY Timestamp DESC
                             
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Timestamp time = rs.getTimestamp("Timestamp");
                String action = rs.getString("Action");
                activity_log_listview.getItems().add(time + " - " + action);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkInVisitor(int userId) {
        String name = visitor_name_field.getText();
        String purpose = visitor_purpose_field.getText();
        if (name.isEmpty() || purpose.isEmpty()) {
            showAlert("Input Error", "Enter visitor name and purpose.");
            return;
        }
        insertLog(userId, "Visitor " + name + " checked in. Purpose: " + purpose);
        visitor_name_field.clear();
        visitor_purpose_field.clear();
        refreshActivityLog(userId);
    }

    private void checkOutVisitor(int userId) {
        String name = visitor_name_field.getText();
        if (name.isEmpty()) {
            showAlert("Input Error", "Enter visitor name.");
            return;
        }
        insertLog(userId, "Visitor " + name + " checked out.");
        visitor_name_field.clear();
        refreshActivityLog(userId);
    }

    private void reportIncident(int userId) {
        String incident = incident_report_area.getText();
        if (incident.isEmpty()) {
            showAlert("Input Error", "Describe the incident.");
            return;
        }
        insertLog(userId, "Incident reported: " + incident);
        incident_report_area.clear();
        refreshActivityLog(userId);
    }

    private void triggerEmergencyAlert(int userId) {
        insertLog(userId, "EMERGENCY ALERT triggered by guard!");
        refreshActivityLog(userId);
        showAlert("Emergency", "Emergency alert triggered!");
    }

    private void insertLog(int userId, String action) {
        String getHomeQuery = "SELECT HomeId FROM Home WHERE SecurityGuardId = ?";
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

            Stage currentStage = (Stage) logout_btn2.getScene().getWindow();
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
