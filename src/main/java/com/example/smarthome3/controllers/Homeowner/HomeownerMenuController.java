package com.example.smarthome3.controllers.Homeowner;

import com.example.smarthome3.Models.Model;
import com.example.smarthome3.Database.UserSession;
import com.example.smarthome3.Database.DatabaseConnector;
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

    private DatabaseConnector dbConnector;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dbConnector = new DatabaseConnector();
        System.out.println("✅ HomeownerMenuController initialized.");
        addListeners();
    }

    private void addListeners() {
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

        report_btn.setOnAction(event -> {
            System.out.println("Report button clicked");
            Model.getInstance().getViewFactory().getHomeownerSelectedMenuItem().set("Report");
        });

        logout_btn.setOnAction(event -> logoutAndRedirectToLogin());
    }

    private void logoutAndRedirectToLogin() {
        // Clear user session
        UserSession.clearUser();

        // Reset model data
        Model model = Model.getInstance();
        model.getViewFactory().setLoggedInUser(null);
        model.setUsername(null);
        model.setUserRole(null);
        model.clearUserData();

        // Close database connection
        if (dbConnector != null) {
            dbConnector.closeConnection();
        }

        System.out.println("✅ User session, model data, and database connection cleared.");

        try {
            // Load login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Login");
            stage.show();

            // Close current window
            Stage currentStage = (Stage) logout_btn.getScene().getWindow();
            currentStage.close();

            System.out.println("✅ Redirected to login page.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Error loading Login page: " + e.getMessage());
        }
    }
}