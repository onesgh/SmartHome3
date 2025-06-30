package com.example.smarthome3.Database;

public class User {
    public int id;
    public String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /*public User() {
        this.id = id;
        this.name = "";
    }*/

    public int getID() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
