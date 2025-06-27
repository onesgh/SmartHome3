package com.example.smarthome3.Database;

public class UserSession {
    private static UserSession instance;
    private User user;

    private UserSession(User user) {
        this.user = user;
    }

    // Initialize or update singleton with a user
    public static UserSession getInstance(User user) {
        if (instance == null) {
            instance = new UserSession(user);
        } else {
            instance.setUser(user); // Update user if instance already exists
        }
        return instance;
    }

    // Get existing singleton instance
    public static UserSession getInstance() {
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // ✅ Properly clear the current session
    public static void clearUser() {
        if (instance != null) {
            instance.user = null;
            instance = null;
        }
    }
}
