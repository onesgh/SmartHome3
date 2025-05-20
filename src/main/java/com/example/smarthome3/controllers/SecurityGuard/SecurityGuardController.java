package com.example.smarthome3.controllers.SecurityGuard;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("✅ Security Guard Dashboard Initialized");

        // Check if UI elements are correctly linked
        if (activity_log_listview == null) System.out.println("❌ Error: ListView not found!");
        if (checkin_btn == null) System.out.println("❌ Error: Check-In Button not found!");
        if (checkout_btn == null) System.out.println("❌ Error: Check-Out Button not found!");
        if (report_incident_btn == null) System.out.println("❌ Error: Report Button not found!");

        // Example: Load activity log (Replace this with real data retrieval)
        activity_log_listview.getItems().addAll(
                "Visitor John checked in at 10:30 AM",
                "Incident reported near Gate A",
                "Visitor Alice checked out at 12:45 PM"
        );

        // Handle Check-In button
        checkin_btn.setOnAction(event -> checkInVisitor());

        // Handle Check-Out button
        checkout_btn.setOnAction(event -> checkOutVisitor());

        // Handle Incident Report button
        report_incident_btn.setOnAction(event -> reportIncident());

        // Handle Refresh button
        refresh_btn.setOnAction(event -> refreshActivityLog());

        // Handle Emergency Alert button
        emergency_alert_btn.setOnAction(event -> triggerEmergencyAlert());

        // Handle Logout button
        if (logout_btn2 != null) {
            logout_btn2.setOnAction(event -> logoutAndRedirectToLogin());
        } else {
            System.out.println("❌ Logout button is not linked!");
        }
    }

    // Check-In Method
    private void checkInVisitor() {
        String visitorName = visitor_name_field.getText();
        String visitPurpose = visitor_purpose_field.getText();

        if (visitorName.isEmpty() || visitPurpose.isEmpty()) {
            showAlert("Error", "Please enter visitor name and purpose.");
            return;
        }

        String entry = "Visitor " + visitorName + " checked in. Purpose: " + visitPurpose;
        activity_log_listview.getItems().add(entry);
        System.out.println("✅ " + entry);

        visitor_name_field.clear();
        visitor_purpose_field.clear();
    }

    // Check-Out Method
    private void checkOutVisitor() {
        String visitorName = visitor_name_field.getText();

        if (visitorName.isEmpty()) {
            showAlert("Error", "Please enter visitor name.");
            return;
        }

        String entry = "Visitor " + visitorName + " checked out.";
        activity_log_listview.getItems().add(entry);
        System.out.println("✅ " + entry);

        visitor_name_field.clear();
    }

    // Report Incident Method
    private void reportIncident() {
        String incident = incident_report_area.getText();

        if (incident.isEmpty()) {
            showAlert("Error", "Please describe the incident.");
            return;
        }

        activity_log_listview.getItems().add("🚨 Incident Reported: " + incident);
        System.out.println("🚨 " + incident);

        incident_report_area.clear();
    }

    // Refresh Log Method
    private void refreshActivityLog() {
        System.out.println("🔄 Activity log refreshed.");
        activity_log_listview.refresh();
    }

    // Emergency Alert Method
    private void triggerEmergencyAlert() {
        showAlert("Emergency Alert", "🚨 Emergency Alert Triggered! Security is responding.");
        System.out.println("🚨 Emergency Alert Triggered!");
    }

    // Utility Method: Show Alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Logout and Redirect to Login Page
    private void logoutAndRedirectToLogin() {
        try {
            // Load the login scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Login Dashboard");
            stage.show();

            // Close the current Security Guard window
            Stage currentStage = (Stage) logout_btn2.getScene().getWindow();
            currentStage.close();

            System.out.println("Redirecting to login page...");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Error loading Login page.");
        }
    }
}
