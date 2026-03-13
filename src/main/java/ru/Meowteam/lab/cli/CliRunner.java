package ru.Meowteam.lab.cli;

import ru.Meowteam.lab.domain.Reagent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CliRunner {

    // Временное хранилище, пока негде просто зранить данные, так что потом удалим
    private final List<Reagent> reagentStorage = new ArrayList<>();
    private long idCounter = 1;

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=========================================");
        System.out.println(" Добро пожаловать в Laboratory System! ");
        System.out.println(" Введите 'help' для списка команд. ");
        System.out.println("=========================================");

        boolean isRunning = true;

        while (isRunning) {
            System.out.print("> ");

            String inputLine = scanner.nextLine().trim();

            if (inputLine.isEmpty()) {
                continue;
            }

            List<String> args = CommandParser.parseArgs(inputLine);
            String command = args.get(0).toLowerCase();

            try {
                // тут начинается обработка команд!!!!!
                switch (command) {
                    case "help":
                        printHelp();
                        break;

                    case "exit":
                    case "quit":
                        System.out.println("Завершение работы...");
                        isRunning = false;
                        break;

                    case "add_reagent":
                        handleAddReagent(args);
                        break;

                    case "list_reagents":
                        handleListReagents();
                        break;

                    default:
                        System.out.println("Неизвестная команда: '" + command + "'. Введите 'help' для справки.");
                        break;
                }
            } catch (Exception e) {

                System.out.println("Ошибка при выполнении команды: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private void printHelp() {
        System.out.println("Доступные команды:");
        System.out.println("  help                                 - Показать возможные действия");
        System.out.println("  add_reagent \"Название\" [Формула]     - Добавить новый реагент");
        System.out.println("  list_reagents                        - Показать список всех реагентов");
        System.out.println("  exit (или quit)                      - Завершить программу");
    }

    private void handleAddReagent(List<String> args) {
        if (args.size() < 2) {
            System.out.println("Ошибка: не указано название реагента. Пример: add_reagent \"Хлорид натрия\" \"NaCl\"");
            return;
        }

        String name = args.get(1);
        String formula = (args.size() > 2) ? args.get(2) : "Не указана";

        Reagent reagent = new Reagent();
        reagent.setId(idCounter++);
        reagent.setName(name);
        reagent.setFormula(formula);
        reagent.setCreatedAt(Instant.now());
        reagent.setUpdatedAt(Instant.now());

        reagentStorage.add(reagent);

        System.out.println("Реагент был добавлен: [ID: " + reagent.getId() + "] " + reagent.getName() + " (" + reagent.getFormula() + ")");
    }

    private void handleListReagents() {
        if (reagentStorage.isEmpty()) {
            System.out.println("Список реагентов пуст.");
            return;
        }

        System.out.println("Список реагентов:");
        for (Reagent r : reagentStorage) {
            System.out.printf(" - [%d] %s | Формула: %s | Создан: %s\n",
                    r.getId(), r.getName(), r.getFormula(), r.getCreatedAt());
        }
    }
}