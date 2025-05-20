package com.example.smarthome3.controllers.Homeowner;

import com.example.smarthome3.Models.Model;
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
    @FXML
    public Button dashboard_btn;
    @FXML
    public Button light_btn;
    @FXML
    public Button motion_btn;
    @FXML
    public Button temperature_btn;
    @FXML
    public Button humidity_btn;
    @FXML
    public Button profile_btn;
    @FXML
    public Button logout_btn;
    @FXML
    public Button report_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("HomeownerMenuController initialized.");
        addListeners();
    }

    private void addListeners() {
        // Add listener for logout button
        if (logout_btn != null) {
            logout_btn.setOnAction(event -> {
                System.out.println("Logout button clicked");
                logoutAndRedirectToLogin();
            });
        } else {
            System.out.println("❌ logout_btn is not linked in FXML!");
        }

        // Other button listeners (example)
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

        // You can add other listeners similarly...
    }

    private void logoutAndRedirectToLogin() {
        // You can perform any necessary logout actions here like clearing user data
        // For example: Model.getInstance().clearUserSession();

        try {
            // Load the login scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Login Dashboard");
            stage.show();

            // Close the current Homeowner window
            Stage currentStage = (Stage) logout_btn.getScene().getWindow();
            currentStage.close();

            System.out.println("Redirecting to login page...");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Error loading Login page.");
        }
    }
}
