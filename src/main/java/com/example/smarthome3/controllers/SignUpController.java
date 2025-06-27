package com.example.smarthome3.controllers;

import com.example.smarthome3.Database.DatabaseConnector;
import com.example.smarthome3.Database.UserDAO;
import com.example.smarthome3.Views.AccountType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

    @FXML
    public Label HaveAccount;

    @FXML
    private ChoiceBox<String> acc_selector;

    @FXML
    private TextField emailField;

    @FXML
    private Button signUpBtn;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField usernameField;

    private UserDAO userDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseConnector dbConnector = new DatabaseConnector();
        userDAO = new UserDAO(dbConnector);

        acc_selector.setItems(FXCollections.observableArrayList(
                AccountType.HOMEOWNER.name(),
                AccountType.TECHNICIAN.name(),
                AccountType.SECURITYGUARD.name()
        ));
        acc_selector.setValue(AccountType.HOMEOWNER.name());

        signUpBtn.setOnAction(event -> onSignup());
        HaveAccount.setOnMouseClicked(event -> openLoginDashboard());
        HaveAccount.setStyle("-fx-text-fill: blue; -fx-underline: true; -fx-cursor: hand;");
    }

    private void onSignup() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String email = emailField.getText().trim();
        String accountType = acc_selector.getValue();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "Please fill all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match.");
            return;
        }

        // Store plain password (no hashing)
        String plainPassword = password;

        boolean isCreated = userDAO.createUser(username, email, plainPassword, accountType);
        if (isCreated) {
            showAlert(Alert.AlertType.INFORMATION, "Sign-Up Successful", "You have successfully signed up!");
            openLoginDashboard();
        } else {
            showAlert(Alert.AlertType.ERROR, "Sign-Up Failed", "Could not create user. Please try again.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openLoginDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Login Dashboard");
            stage.show();
            ((Stage) HaveAccount.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the login page.");
        }
    }
}
