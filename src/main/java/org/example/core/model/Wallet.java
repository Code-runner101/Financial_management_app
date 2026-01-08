package org.example.core.model;

import java.util.*;

public class Wallet {

    public List<Transaction> transactions = new ArrayList<>();
    public Map<String, Category> categories = new HashMap<>();

    public void addCategory(String name, CategoryType type) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Название категории не может быть пустым");
        }
        if (categories.containsKey(name)) {
            throw new RuntimeException("Категория уже существует");
        }
        categories.put(name, new Category(name, type));
    }

    public Collection<Category> getCategories() {
        return categories.values();
    }

    public double totalIncome() {
        return transactions.stream()
                .filter(t -> t.type == TransactionType.INCOME)
                .mapToDouble(t -> t.amount)
                .sum();
    }

    public double totalExpense() {
        return transactions.stream()
                .filter(t -> t.type == TransactionType.EXPENSE)
                .mapToDouble(t -> t.amount)
                .sum();
    }

    public double expenseByCategory(String category) {
        return transactions.stream()
                .filter(t -> t.type == TransactionType.EXPENSE
                        && t.category.equals(category))
                .mapToDouble(t -> t.amount)
                .sum();
    }

    public Map<String, Double> incomesByCategories() {
        Map<String, Double> result = new HashMap<>();
        for (Transaction t : transactions) {
            if (t.type == TransactionType.INCOME) {
                result.merge(t.category, t.amount, Double::sum);
            }
        }
        return result;
    }

    public void renameCategory(String oldName, String newName) {
        if (!categories.containsKey(oldName)) {
            throw new RuntimeException("Категория не найдена: " + oldName);
        }
        if (categories.containsKey(newName)) {
            throw new RuntimeException("Категория уже существует: " + newName);
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new RuntimeException("Новое имя категории некорректно");
        }

        Category cat = categories.remove(oldName);
        cat.name = newName;
        categories.put(newName, cat);

        // обновляем транзакции
        for (Transaction t : transactions) {
            if (t.category.equals(oldName)) {
                t.category = newName;
            }
        }
    }

    public void removeCategory(String name) {
        if (!categories.containsKey(name)) {
            throw new RuntimeException("Категория не найдена: " + name);
        }

        boolean hasTransactions = transactions.stream()
                .anyMatch(t -> t.category.equals(name));

        if (hasTransactions) {
            throw new RuntimeException(
                    "Нельзя удалить категорию с существующими операциями"
            );
        }

        categories.remove(name);
    }
}
