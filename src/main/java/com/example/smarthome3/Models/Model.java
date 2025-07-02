package com.example.smarthome3.Models;

import com.example.smarthome3.Views.ViewFactory;
import com.example.smarthome3.Views.AccountType;

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
    public static void resetInstance() {
        model = new Model();
        System.out.println("✅ Model instance reset.");
    }


    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public AccountType setUserRole() {
        return userRole;
    }

    public void setUserRole(AccountType userRole) {
        this.userRole = userRole;
    }

    // ✅ GETTER & SETTER for username (changed from email)
    public String setUsername() {
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


}
