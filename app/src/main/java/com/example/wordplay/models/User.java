package com.example.wordplay.models;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String login;
    private String password;
    private String level;
    private String token;
    private String newPassword;

    public User(String login, String password, String level) {
        this.login = login;
        this.password = password;
        this.level = level;
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public User() {}

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
