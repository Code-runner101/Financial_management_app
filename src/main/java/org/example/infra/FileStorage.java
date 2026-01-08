package org.example.infra;

import com.google.gson.Gson;
import org.example.core.model.Wallet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileStorage {
    private final Gson gson = new Gson();

    public void save(String login, Wallet wallet) {
        try {
            Files.writeString(Path.of(login + ".json"), gson.toJson(wallet));
        } catch (IOException e) {
            System.out.println("Ошибка сохранения");
        }
    }

    public Wallet load(String login) {
        try {
            Path path = Path.of(login + ".json");
            if (!Files.exists(path)) return new Wallet();
            return gson.fromJson(Files.readString(path), Wallet.class);
        } catch (IOException e) {
            return new Wallet();
        }
    }
}
