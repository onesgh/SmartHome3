package com.example.smarthome3.Database;

public class UserSession {
    private static UserSession instance;
    private User currentUser;

    // Private constructor (singleton)
    private UserSession(User currentUser) {
        this.currentUser = currentUser;
    }

    // Get existing session instance (null if not initialized)
    public static synchronized UserSession getInstance() {
        return instance;
    }

    // Create or update session with a user
    public static synchronized UserSession getInstance(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (instance == null) {
            System.out.println("[UserSession] Creating new session for: " + user.getName());
            instance = new UserSession(user);
        } else {
            System.out.println("[UserSession] Updating session to user: " + user.getName());
            instance.currentUser = user;
        }

        return instance;
    }

    public static void startSession(User user) {
        instance = new UserSession(user);  // ✅ Always create a new session
    }

    // Get the current user object
    public User getCurrentUser() {
        return currentUser;
    }



    // Clear the session (logout)
    public static void clearUser() {
        System.out.println("[UserSession] Clearing user session.");
        instance = null;
    }
}
