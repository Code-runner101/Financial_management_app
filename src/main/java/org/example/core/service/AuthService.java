package org.example.core.service;

import org.example.core.model.User;
import org.example.infra.UserStorage;

import java.util.Map;

public class AuthService {

    private final Map<String, User> users;
    private final UserStorage storage;

    public AuthService(UserStorage storage) {
        this.storage = storage;
        this.users = storage.loadUsers();
    }

    public User login(String login, String password) {
        User user = users.get(login);
        if (user == null || !user.password.equals(password)) {
            throw new RuntimeException("Неверный логин или пароль");
        }
        return user;
    }

    public User register(String login, String password) {
        if (login == null || login.trim().isEmpty()) throw new RuntimeException("Логин пустой");
        if (password == null || password.trim().isEmpty()) throw new RuntimeException("Пароль пустой");

        // Считываем актуальный файл с диска
        Map<String, User> diskUsers = storage.loadUsers();
        diskUsers.putAll(users); // объединяем с локальными пользователями

        if (diskUsers.containsKey(login)) {
            throw new RuntimeException("Пользователь уже существует");
        }

        User user = new User(login, password);
        diskUsers.put(login, user);

        storage.saveUsers(diskUsers);

        // Обновляем локальную Map
        users.put(login, user);

        return user;
    }
}
