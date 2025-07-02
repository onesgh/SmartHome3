package com.example.smarthome3.Models;

import com.example.smarthome3.Views.ViewFactory;
import com.example.smarthome3.Views.AccountType;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private AccountType userRole;
    private String username;
    private AccountType loginAccountType = AccountType.HOMEOWNER;

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

    public void setUserRole(AccountType userRole) {
        this.userRole = userRole;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
