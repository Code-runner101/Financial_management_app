package org.example;

import org.example.cli.CommandLoop;
import org.example.core.service.AuthService;
import org.example.infra.FileStorage;
import org.example.infra.UserStorage;

public class Main {
    public static void main(String[] args) {
        UserStorage userStorage = new UserStorage();
        AuthService authService = new AuthService(userStorage);
        FileStorage storage = new FileStorage();
        CommandLoop loop = new CommandLoop(authService, storage);
        loop.start();
    }
}