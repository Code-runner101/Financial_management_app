package org.example.cli;

import org.example.core.model.CategoryType;
import org.example.core.model.User;
import org.example.core.service.AuthService;
import org.example.core.service.StatisticsService;
import org.example.core.service.WalletService;
import org.example.infra.FileStorage;

import java.util.Scanner;

public class CommandLoop {

    private final AuthService auth;
    private final WalletService walletService = new WalletService();
    private final StatisticsService stats = new StatisticsService();
    private final FileStorage storage;
    private User current;

    public CommandLoop(AuthService auth, FileStorage storage) {
        this.auth = auth;
        this.storage = storage;
    }

    public void start() {
        Scanner sc = new Scanner(System.in);

        // auth
        while (true) {
            try {
                System.out.println("Введите команду: login / register");
                String mode = sc.nextLine().trim().toLowerCase();

                if(mode.isEmpty()) {
                    System.out.println("Введите команду: login / register");
                    continue;
                }

                if(!mode.equals("login") && !mode.equals("register")) {
                    System.out.println("Неизвестная команда");
                    continue;
                }

                System.out.print("login: ");
                String login = sc.nextLine().trim();

                System.out.print("password: ");
                String password = sc.nextLine().trim();

                if (mode.equals("login")) {
                    current = auth.login(login, password);
                } else if (mode.equals("register")) {
                    current = auth.register(login, password);
                }

                // загрузка кошелька
                current.wallet = storage.load(current.login);
                break;

            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                System.out.println("Повторите ввод или зарегистрируйтесь");
            }
        }

        System.out.println("help — список команд");

        // основной цикл
        while (true) {
            System.out.print("> ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] cmd = line.split(" ");

            try {
                switch (cmd[0]) {
                    case "add-category" -> {
                        if (cmd.length != 3) {
                            System.out.println("Использование: add-category <категория> <income|expense>");
                            break;
                        }

                        try {
                            CategoryType type = CategoryType.valueOf(cmd[2].toUpperCase());
                            walletService.addCategory(current, cmd[1], type);
                        } catch (Exception e) {
                            System.out.println("Ошибка ввода: " + "Использование: add-category <категория> <income|expense>");
                        }
                    }
                    case "list-categories" -> walletService.listCategories(current);
                    case "rename-category" -> {
                        if (cmd.length != 3) {
                            System.out.println("Использование: rename-category <старое> <новое>");
                            break;
                        }
                        walletService.renameCategory(current, cmd[1], cmd[2]);
                    }
                    case "remove-category" -> {
                        if (cmd.length != 2) {
                            System.out.println("Использование: remove-category <категория>");
                            break;
                        }
                        walletService.removeCategory(current, cmd[1]);
                    }
                    case "add-income" -> {
                        if (cmd.length != 3) {
                            System.out.println("Использование: add-income <cat> <sum>");
                            break;
                        }
                        double amount = Double.parseDouble(cmd[2]);

                        if (Math.round(amount * 100) != amount * 100) {
                            System.out.println("Сумма должна быть с точностью до 2 знаков после запятой");
                            break;
                        }
                        walletService.addIncome(current, cmd[1],
                                Double.parseDouble(cmd[2]));
                    }
                    case "add-expense" -> {
                        if (cmd.length != 3) {
                            System.out.println("Использование: add-expense <cat> <sum>");
                            break;
                        }
                        double amount = Double.parseDouble(cmd[2]);

                        if (Math.round(amount * 100) != amount * 100) {
                            System.out.println("Сумма должна быть с точностью до 2 знаков после запятой");
                            break;
                        }
                        walletService.addExpense(current, cmd[1],
                                Double.parseDouble(cmd[2]));
                    }
                    case "set-budget" -> {
                        if (cmd.length != 3) {
                            System.out.println("Использование: set-budget <cat> <sum>");
                            break;
                        }
                        walletService.setBudget(current, cmd[1],
                                Double.parseDouble(cmd[2]));
                    }
                    case "stats" -> stats.print(current);
                    case "help" -> printHelp();
                    case "exit" -> {
                        storage.save(current.login, current.wallet);
                        return;
                    }
                    default -> System.out.println("Неизвестная команда");
                }
            } catch (NumberFormatException e) {
                System.out.println("Сумма должна быть числом");
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void printHelp() {
        System.out.println("""
        === СПРАВКА ПО КОМАНДАМ ===

        add-category <название_категории> <income|expense>
          Создаёт новую категорию
          Пример: add-category еда expense

        list-categories
          Показывает все добавленные категории

        rename-category <старое> <новое>
          Переименовывает категорию

        remove-category <категория>
          Удаляет категорию (если нет операций)

        add-income <категория> <сумма>
          Добавляет доход в указанную категорию
          Пример: add-income зарплата 50000

        add-expense <категория> <сумма>
          Добавляет расход в указанную категорию
          Пример: add-expense продукты 3000

        set-budget <категория> <сумма>
          Устанавливает лимит бюджета для категории
          Пример: set-budget ремонт_машины 5000

        stats
          Показывает статистику доходов, расходов и бюджеты по категориям

        exit
          Сохраняет данные и выходит из приложения

        help
          Показывает эту справку

        Советы:
          - Все суммы вводятся числами без валютного символа
          - Категории чувствительны к регистру
          - Если название категории состоит из нескольких слов -> запись в формате "название_категории"
        ==============================
        """);
    }
}
