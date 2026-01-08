package org.example.core.model;

public class User {
    public String login;
    public String password;
    public Wallet wallet = new Wallet();

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
