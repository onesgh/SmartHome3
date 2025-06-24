package com.example.smarthome3.controllers;

import com.example.smarthome3.Models.Model;
import com.example.smarthome3.Database.DatabaseConnector;
import com.example.smarthome3.Database.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField user_address_lbl; // Username field

    @FXML
    private Button login_btn;

    @FXML
    private Button Signin_btn;

    @FXML
    private TextField password_fld;

    private UserDAO userDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseConnector dbConnector = new DatabaseConnector();
        userDAO = new UserDAO(dbConnector);

        // Set the login button action
        login_btn.setOnAction(event -> onLogin());

        // Sign up button opens sign-up window
        Signin_btn.setOnAction(event -> openSignUpWindow());
    }

    private void onLogin() {
        String username = user_address_lbl.getText().trim();
        String password = password_fld.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Login Error", "Username or password field is empty.");
            return;
        }

        try {
            // Fetch user by username
            ResultSet result = userDAO.getUserByUsername(username);
            if (result != null && result.next()) {
                String storedPassword = result.getString("password");
                String roleFromDB = result.getString("accountType").toUpperCase();

                // Password check
                if (storedPassword.equals(password)) {
                    Model.getInstance().getViewFactory().setLoggedInUser(username);
                    Model.getInstance().setUsername(username);
                    Model.getInstance()
                            .setUserRole(Enum.valueOf(com.example.smarthome3.Views.AccountType.class, roleFromDB));

                    System.out.println(" Login successful as " + roleFromDB);
                    openDashboard(roleFromDB);
                } else {
                    showAlert(AlertType.ERROR, "Login Error", "Incorrect password.");
                }
            } else {
                showAlert(AlertType.ERROR, "Login Error", "User not found.");
            }
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Error occurred while accessing the database.");
            e.printStackTrace();
        }
    }

    private void openDashboard(String accountType) {
        Stage stage = (Stage) login_btn.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);

        switch (accountType) {
            case "HOMEOWNER":
                Model.getInstance().getViewFactory().showHomeownerWindow();
                break;
            case "TECHNICIAN":
                Model.getInstance().getViewFactory().showTechnicianWindow();
                break;
            case "SECURITYGUARD":
                Model.getInstance().getViewFactory().showSecurityGuardWindow();
                break;
            default:
                System.out.println(" Unknown account type.");
                showAlert(AlertType.ERROR, "Unknown Account", "Unknown account type. Please contact support.");
        }
    }

    private void openSignUpWindow() {
        Stage stage = (Stage) Signin_btn.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showSignUpWindow();
    }

    // Alert utility
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}