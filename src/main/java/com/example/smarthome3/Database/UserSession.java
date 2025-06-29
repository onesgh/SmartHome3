package com.example.smarthome3.Database;

public class UserSession {
    private static UserSession instance;
    private static User currentUser;

    public UserSession(User currentUser) {
        this.currentUser = currentUser;
    }

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession(currentUser);
        }
        return instance;
    }

    public static synchronized UserSession getInstance(User user) {
        if (instance == null) {
            instance = new UserSession(currentUser);
        }
        instance.currentUser = user;
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public static void clearUser() {
        if (instance != null) {
            instance.currentUser = null;
        }
    }
}