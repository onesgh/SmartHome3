package com.example.smarthome3.Database;

public class UserSession {
    private static UserSession instance;
    private User currentUser;

    // Private constructor to enforce singleton pattern
    private UserSession(User currentUser) {
        this.currentUser = currentUser;
    }

    // Returns the current instance (if any)
    public static synchronized UserSession getInstance() {
        return instance;
    }

    // Creates or updates the session with a new user
    public static synchronized UserSession getInstance(User user) {
        if (instance == null) {
            instance = new UserSession(user); // ✅ correct: pass the given user
        } else {
            instance.currentUser = user; // ✅ update current user if session already exists
        }
        return instance;
    }

    // Returns the current logged-in user
    public User getCurrentUser() {
        return currentUser;
    }

    // Clears the session completely (used on logout)
    public static void clearUser() {
        instance = null; // ✅ reset the whole session
    }
}
