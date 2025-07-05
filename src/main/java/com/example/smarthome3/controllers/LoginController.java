package com.example.smarthome3.controllers;

import com.example.smarthome3.Models.Model;
import com.example.smarthome3.Database.DatabaseConnector;
import com.example.smarthome3.Database.UserDAO;
import com.example.smarthome3.Database.User;
import com.example.smarthome3.Database.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    public Label DontHaveAccount;

    @FXML
    private TextField user_address_lbl;

    @FXML
    private Button login_btn;

    @FXML
    private TextField password_fld;

    private UserDAO userDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseConnector dbConnector = new DatabaseConnector();
        userDAO = new UserDAO(dbConnector);

        login_btn.setOnAction(event -> onLogin());
        DontHaveAccount.setOnMouseClicked(event -> openSignUpWindow());
        DontHaveAccount.setStyle("-fx-text-fill: blue; -fx-underline: true; -fx-cursor: hand;");
    }

    private void onLogin() {
        String username = user_address_lbl.getText().trim();
        String password = password_fld.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Login Error", "Username or password field is empty.");
            return;
        }

        try {
            ResultSet result = userDAO.getUserByUsername(username);
            if (result != null && result.next()) {
                String storedPassword = result.getString("passwordHash");
                String storedUsername = result.getString("username");
                if (!storedUsername.equals(username)) {
                    showAlert(AlertType.ERROR, "Login Error", "User not found.");
                    return;
                }


                if (storedPassword.equals(password)) {
                    int userId = result.getInt("UserId");

                    // ✅ FULL RESET
                    Model.resetInstance();
                    UserSession.startSession(new User(userId, username));

                    Model model = Model.getInstance();
                    model.setUsername(username);
                    model.setUserRole(Enum.valueOf(com.example.smarthome3.Views.AccountType.class,
                            result.getString("accountType").toUpperCase()));
                    model.getViewFactory().setLoggedInUser(username);

                    openDashboard(result.getString("accountType").toUpperCase());
                } else {
                    showAlert(AlertType.ERROR, "Login Error", "Incorrect password.");
                }
            } else {
                showAlert(AlertType.ERROR, "Login Error", "User not found.");
            }
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Error accessing database: " + e.getMessage());
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
                showAlert(AlertType.ERROR, "Unknown Account", "Unknown account type. Please contact support.");
        }
    }

    private void openSignUpWindow() {
        Stage stage = (Stage) DontHaveAccount.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showSignUpWindow();
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}