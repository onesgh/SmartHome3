package com.example.smarthome3.controllers;

import com.example.smarthome3.Models.Model;
import com.example.smarthome3.Views.AccountType;
import com.example.smarthome3.Database.DatabaseConnector;
import com.example.smarthome3.Database.UserDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
    private ChoiceBox<AccountType> acc_selector;

    @FXML
    private TextField user_address_lbl;  // This now represents the username field

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

        // Populate account types
        acc_selector.setItems(FXCollections.observableArrayList(
                AccountType.HOMEOWNER,
                AccountType.TECHNICIAN,
                AccountType.SECURITYGUARD
        ));

        acc_selector.setValue(Model.getInstance().getViewFactory().getLoginAccountType());
        acc_selector.valueProperty().addListener(observable ->
                Model.getInstance().getViewFactory().setLoginAccountType(acc_selector.getValue())
        );

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

                AccountType selectedRole = acc_selector.getValue();

                // Role mismatch check
                if (!roleFromDB.equals(selectedRole.name())) {
                    showAlert(AlertType.ERROR, "Login Error",
                            "Account type mismatch.\n" +
                                    "You selected: " + selectedRole + "\n" +
                                    "But this user is registered as: " + roleFromDB);
                    return;
                }

                // Password check
                if (storedPassword.equals(password)) {
                    Model.getInstance().getViewFactory().setLoggedInUser(username);
                    Model.getInstance().setUsername(username);
                    Model.getInstance().setUserRole(selectedRole);

                    System.out.println("✅ Login successful as " + roleFromDB);
                    openDashboard(selectedRole);
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

    private void openDashboard(AccountType accountType) {
        Stage stage = (Stage) login_btn.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);

        switch (accountType) {
            case HOMEOWNER -> Model.getInstance().getViewFactory().showHomeownerWindow();
            case TECHNICIAN -> Model.getInstance().getViewFactory().showTechnicianWindow();
            case SECURITYGUARD -> Model.getInstance().getViewFactory().showSecurityGuardWindow();
            default -> {
                System.out.println("❌ Unknown account type.");
                showAlert(AlertType.ERROR, "Unknown Account", "Unknown account type. Please contact support.");
            }
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
