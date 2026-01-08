package org.example.core.service;

import org.example.core.model.*;

public class WalletService {

    public void addCategory(User user, String category, CategoryType type) {
        user.wallet.addCategory(category, type);
        System.out.println("Категория добавлена: " + category);
    }

    public void addIncome(User user, String category, double amount) {
        validate(amount);
        Category cat = getCategory(user, category);

        if (cat.type != CategoryType.INCOME) {
            throw new RuntimeException(
                    "Категория '" + category + "' не предназначена для доходов"
            );
        }

        user.wallet.transactions.add(
                new Transaction(category, amount, TransactionType.INCOME)
        );
    }

    public void addExpense(User user, String category, double amount) {
        validate(amount);
        Category cat = getCategory(user, category);

        if (cat.type != CategoryType.EXPENSE) {
            throw new RuntimeException(
                    "Категория '" + category + "' не предназначена для расходов"
            );
        }

        if (cat.budget <= 0) {
            throw new RuntimeException(
                    "Для категории '" + category + "' не установлен бюджет. Сначала установите бюджет"
            );
        }

        user.wallet.transactions.add(
                new Transaction(category, amount, TransactionType.EXPENSE)
        );

        checkBudget(user, category);
    }

    public void setBudget(User user, String category, double budget) {
        validate(budget);
        Category cat = getCategory(user, category);

        if (cat.type != CategoryType.EXPENSE) {
            throw new RuntimeException("Бюджет можно установить только для расходов");
        }

        cat.budget = budget;
    }

    private void validate(double amount) {
        if (amount <= 0) {
            throw new RuntimeException("Сумма должна быть положительной");
        }
    }

    public void listCategories(User user) {
        if (user.wallet.categories.isEmpty()) {
            System.out.println("Категории не добавлены");
            return;
        }

        System.out.printf("%-15s %-10s %-10s%n", "Категория", "Тип", "Бюджет");
        System.out.println("-------------------------------------");

        user.wallet.getCategories().forEach(cat -> {
            String budget = cat.type == CategoryType.EXPENSE
                    ? String.valueOf(cat.budget)
                    : "-";
            System.out.printf(
                    "%-15s %-10s %-10s%n",
                    cat.name,
                    cat.type,
                    budget
            );
        });
    }

    private void checkBudget(User user, String category) {
        Category cat = user.wallet.categories.get(category);
        if (cat == null || cat.budget <= 0) return;

        double spent = user.wallet.expenseByCategory(category);
        if (spent > cat.budget) {
            System.out.println("Превышен бюджет категории: " + category);
        }
    }

    private Category getCategory(User user, String category) {
        Category cat = user.wallet.categories.get(category);
        if (cat == null) {
            throw new RuntimeException(
                    "Категория '" + category + "' не существует"
            );
        }
        return cat;
    }

    public void renameCategory(User user, String oldName, String newName) {
        user.wallet.renameCategory(oldName, newName);
        System.out.println(
                "Категория '" + oldName + "' переименована в '" + newName + "'"
        );
    }

    public void removeCategory(User user, String name) {
        user.wallet.removeCategory(name);
        System.out.println("Категория удалена: " + name);
    }
}
