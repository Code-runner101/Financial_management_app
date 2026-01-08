package org.example.infra;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.core.model.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class UserStorage {
    private static final String FILE = "users.json";
    private final Gson gson = new Gson();
    private final Type type = new TypeToken<Map<String, User>>() {}.getType();

    public Map<String, User> loadUsers() {
        try {
            if (!Files.exists(Path.of(FILE))) {
                return new HashMap<>();
            }
            String json = Files.readString(Path.of(FILE));
            return gson.fromJson(json, type);
        } catch (IOException e) {
            return new HashMap<>();
        }
    }

    public void saveUsers(Map<String, User> users) {
        try {
            Files.writeString(Path.of(FILE), gson.toJson(users));
        } catch (IOException e) {
            System.out.println("Ошибка сохранения пользователей");
        }
    }
}
