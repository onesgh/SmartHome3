package com.example.smarthome3.Models;

import com.example.smarthome3.Views.ViewFactory;
import com.example.smarthome3.Views.AccountType;
import javafx.stage.Stage;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private AccountType userRole;
    private String username; // Change email to username
    private AccountType loginAccountType = AccountType.HOMEOWNER; // Optional, used for ChoiceBox

    private Model() {
        this.viewFactory = new ViewFactory();
    }

    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public AccountType getUserRole() {
        return userRole;
    }

    public void setUserRole(AccountType userRole) {
        this.userRole = userRole;
    }

    // ✅ GETTER & SETTER for username (changed from email)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // ✅ Optional: Get/set login account type for UI dropdown
    public AccountType getLoginAccountType() {
        return loginAccountType;
    }

    public void setLoginAccountType(AccountType loginAccountType) {
        this.loginAccountType = loginAccountType;
    }

    // ✅ Update registerUser to use username instead of email
    public boolean registerUser(String username, String password, String confirmPassword, AccountType userRole) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || userRole == null) {
            return false;
        }

        if (!password.equals(confirmPassword)) {
            return false;
        }

        // Save user logic here, you can add database save logic

        return true;
    }

    public void closeStage(Stage currentStage) {
    }
}
