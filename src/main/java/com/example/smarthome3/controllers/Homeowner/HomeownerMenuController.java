package com.example.smarthome3.controllers.Homeowner;

import com.example.smarthome3.Models.Model;
import com.example.smarthome3.Database.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeownerMenuController implements Initializable {
    @FXML public Button dashboard_btn;
    @FXML public Button light_btn;
    @FXML public Button motion_btn;
    @FXML public Button temperature_btn;
    @FXML public Button humidity_btn;
    @FXML public Button Alert_btn;
    @FXML public Button logout_btn;
    @FXML public Button report_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("✅ HomeownerMenuController initialized.");
        addListeners();
    }

    private void addListeners() {
        if (logout_btn != null) {
            // ✅ Updated logout button
            logout_btn.setOnAction(event -> {
                System.out.println("Logging out...");
                UserSession.clearUser(); // Clear the current session

                // Close current stage
                Stage currentStage = (Stage) logout_btn.getScene().getWindow();
                currentStage.close();

                try {
                    // Load login screen
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
                    Stage loginStage = new Stage();
                    loginStage.setScene(new Scene(loader.load()));
                    loginStage.setTitle("Login");
                    loginStage.show();

                    System.out.println("✅ Successfully logged out and redirected to login.");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("❌ Failed to load Login.fxml");
                }
            });

            dashboard_btn.setOnAction(event -> {
                System.out.println("Dashboard button clicked");
                Model.getInstance().getViewFactory().getHomeownerSelectedMenuItem().set("Dashboard");
            });

            humidity_btn.setOnAction(event -> {
                System.out.println("Humidity button clicked");
                Model.getInstance().getViewFactory().getHomeownerSelectedMenuItem().set("Humidity");
            });

            temperature_btn.setOnAction(event -> {
                System.out.println("Temperature button clicked");
                Model.getInstance().getViewFactory().getHomeownerSelectedMenuItem().set("Temperature");
            });

            motion_btn.setOnAction(event -> {
                System.out.println("Motion button clicked");
                Model.getInstance().getViewFactory().getHomeownerSelectedMenuItem().set("Motion");
            });

            light_btn.setOnAction(event -> {
                System.out.println("Light button clicked");
                Model.getInstance().getViewFactory().getHomeownerSelectedMenuItem().set("Light");
            });

            Alert_btn.setOnAction(event -> {
                System.out.println("Alert button clicked");
                Model.getInstance().getViewFactory().getHomeownerSelectedMenuItem().set("Alert");
            });
        }
    }


    private void logoutAndRedirectToLogin() {
        // ✅ Clear the current user session
        UserSession.getInstance().clearUser();
        System.out.println("✅ User session cleared.");

        try {
            // ✅ Load the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Login");
            stage.show();

            // ✅ Close current window
            Stage currentStage = (Stage) logout_btn.getScene().getWindow();
            currentStage.close();

            System.out.println("✅ Redirected to login page.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Error loading Login page.");
        }
    }
}

