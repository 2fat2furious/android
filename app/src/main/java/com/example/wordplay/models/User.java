package com.example.wordplay.models;

public class User {
    private String id;
    private String login;
    private String password;
    private String level;
    private String token;

    public User(String login, String password, String level) {
        this.login = login;
        this.password = password;
        this.level = level;
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
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
