package org.example.core.service;

import org.example.core.model.CategoryType;
import org.example.core.model.User;

public class StatisticsService {

    public void print(User user) {
        System.out.println("=== СТАТИСТИКА ===\n");

        printIncomeSummary(user);
        printExpenseSummary(user);
        printWarnings(user);
    }

    private void printIncomeSummary(User user) {
        System.out.printf("Общий доход: %.2f%n%n", user.wallet.totalIncome());

        if (user.wallet.incomesByCategories().isEmpty()) {
            System.out.println("Доходы отсутствуют\n");
            return;
        }

        System.out.println("Доходы по категориям:");
        System.out.printf("%-15s %-10s%n", "Категория", "Сумма");
        System.out.println("-------------------------");

        user.wallet.incomesByCategories()
                .forEach((cat, sum) ->
                        System.out.printf("%-15s %-10.2f%n", cat, sum)
                );

        System.out.println();
    }

    private void printExpenseSummary(User user) {
        System.out.printf("Общие расходы: %.2f%n%n", user.wallet.totalExpense());

        System.out.println("Расходы и бюджеты:");
        System.out.printf(
                "%-15s %-10s %-12s %-10s%n",
                "Категория", "Бюджет", "Потрачено", "Остаток"
        );
        System.out.println("--------------------------------------------------");

        user.wallet.categories.values().stream()
                .filter(c -> c.type == CategoryType.EXPENSE)
                .forEach(cat -> {
                    double spent = user.wallet.expenseByCategory(cat.name);
                    double remaining = cat.budget - spent;

                    System.out.printf(
                            "%-15s %-10.2f %-12.2f %-10.2f%n",
                            cat.name,
                            cat.budget,
                            spent,
                            remaining
                    );
                });

        System.out.println();
    }

    private void printWarnings(User user) {
        if (user.wallet.totalExpense() > user.wallet.totalIncome()) {
            System.out.println("Расходы превышают доходы!");
        }
    }
}
