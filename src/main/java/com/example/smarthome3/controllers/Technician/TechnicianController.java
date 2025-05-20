package com.example.smarthome3.controllers.Technician;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TechnicianController implements Initializable {
    @FXML public ListView<String> request_listview;
    @FXML public Button resolve_btn;
    @FXML public Button Logout_btn1;
    @FXML public Button reject_btn;
    @FXML public Button refresh_btn;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("✅ Technician Dashboard Loaded");

        // Example: Load maintenance requests (You can replace this with real database values)
        request_listview.getItems().addAll(
                "Fix Living Room Light",
                "Check Humidity Sensor",
                "Replace Motion Sensor Battery"
        );

        // Resolve button action
        resolve_btn.setOnAction(event -> {
            String selectedRequest = request_listview.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                System.out.println("✅ Resolved: " + selectedRequest);
                request_listview.getItems().remove(selectedRequest);
            }
        });
        reject_btn.setOnAction(event -> {
            String selectedRequest = request_listview.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                System.out.println("✅ Rejected: " + selectedRequest);
                request_listview.getItems().remove(selectedRequest);
            }
        });
        refresh_btn.setOnAction(event -> {
            String selectedRequest = request_listview.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                System.out.println("✅ Refreshed: " + selectedRequest);
                request_listview.getItems().remove(selectedRequest);
            }
        });



        // Logout button action
        if (Logout_btn1 != null) {
            Logout_btn1.setOnAction(event -> logoutAndRedirectToLogin());
        } else {
            System.out.println("❌ Logout button is not linked!");
        }
    }

    private void logoutAndRedirectToLogin() {
        try {
            // Load the login scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Login Dashboard");
            stage.show();

            // Close the current Technician window
            Stage currentStage = (Stage) Logout_btn1.getScene().getWindow();
            currentStage.close();

            System.out.println("Redirecting to login page...");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Error loading Login page.");
        }
    }
}
