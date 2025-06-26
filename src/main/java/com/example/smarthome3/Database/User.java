package com.example.smarthome3.Database;

public class User {
    private int id;
    private String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public User() {
        this.id = -1;
        this.name = "";
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }
}
