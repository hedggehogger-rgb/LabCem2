package ru.Meowteam.lab.cli;

import ru.Meowteam.lab.domain.BatchStatus;
import ru.Meowteam.lab.domain.BatchUnit;
import ru.Meowteam.lab.domain.Reagent;
import ru.Meowteam.lab.domain.ReagentBatch;
import ru.Meowteam.lab.repository.BatchRepository;
import ru.Meowteam.lab.repository.ReagentRepository;

import java.time.Instant;
import java.util.List;
import java.util.Scanner;

public class CliRunner {

    private final ReagentRepository reagentRepo = new ReagentRepository();
    private final BatchRepository batchRepo = new BatchRepository();

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=========================================");
        System.out.println(" Добро пожаловать в Laboratory System! ");
        System.out.println(" Введите 'help' для списка команд. ");
        System.out.println("=========================================");

        boolean isRunning = true;

        while (isRunning) {
            System.out.print("\n> ");
            String inputLine = scanner.nextLine().trim();

            if (inputLine.isEmpty()) continue;

            List<String> args = CommandParser.parseArgs(inputLine);
            String command = args.get(0).toLowerCase();

            try {
                switch (command) {
                    case "help": printHelp(); break;
                    case "exit":
                    case "quit":
                        System.out.println("Завершение работы.");
                        isRunning = false;
                        break;

                    // --- Команды Реагентов ---
                    case "reag_add": handleReagAdd(args); break;
                    case "reag_list": handleReagList(); break;

                    // --- Команды Партий ---
                    case "batch_add": handleBatchAdd(args); break;
                    case "batch_list": handleBatchList(); break;
                    case "batch_show": handleBatchShow(args); break;
                    case "batch_update": handleBatchUpdate(args); break;
                    case "batch_archive": handleBatchArchive(args); break;

                    // --- Отчеты ---
                    case "stock_report": handleStockReport(); break;

                    default:
                        System.out.println("Неизвестная команда: '" + command + "'. Введите 'help' для вывода меню.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Ошибка при выполнении команды: " + e.getMessage());
                System.out.println("Проверьте правильность введенных данных (например, числа вместо текста).");
            }
        }
        scanner.close();
    }

    private void printHelp() {
        System.out.println("\n === СПИСОК ДОСТУПНЫХ КОМАНД === ");
        System.out.println("\n РЕАГЕНТЫ:");
        System.out.println("  reag_add \"Название\" \"Формула\"        - Добавить новый реагент");
        System.out.println("  reag_list                            - Показать список всех реагентов");

        System.out.println("\n ПАРТИИ:");
        System.out.println("  batch_add <ID_Реагента> \"Этикетка\" <Кол-во> <G|ML> \"Место\" - Добавить новую партию");
        System.out.println("  batch_list                           - Показать список всех партий");
        System.out.println("  batch_show <ID_Партии>               - Показать подробные детали партий");
        System.out.println("  batch_update <ID_Партии> <Кол-во>    - Обновить текущий остаток в партии");
        System.out.println("  batch_archive <ID_Партии>            - Отправить партию в архив (списать)");

        System.out.println("\n ОТЧЕТЫ:");
        System.out.println("  stock_report                         - Показать сводку по складу");

        System.out.println("\n ПРОЧЕЕ:");
        System.out.println("  help, exit\n");
    }

    // РЕАГЕНТЫ

    private void handleReagAdd(List<String> args) {
        if (args.size() < 2) {
            System.out.println("Ошибка использования. Пример: reag_add \"Хлорид натрия\" \"NaCl\"");
            return;
        }
        Reagent reagent = new Reagent();
        reagent.setName(args.get(1));
        reagent.setFormula(args.size() > 2 ? args.get(2) : "Не указана");
        reagent.setCreatedAt(Instant.now());
        reagent.setUpdatedAt(Instant.now());

        reagentRepo.save(reagent);
        System.out.println("Реагент успешно добавлен: [ID: " + reagent.getId() + "] " + reagent.getName());
    }

    private void handleReagList() {
        List<Reagent> reagents = reagentRepo.findAll();
        if (reagents.isEmpty()) {
            System.out.println("Справочник реагентов пока пуст.");
            return;
        }
        System.out.println("\n СПИСОК РЕАГЕНТОВ:");
        for (Reagent r : reagents) {
            System.out.printf("[%d] %s | Формула: %s\n", r.getId(), r.getName(), r.getFormula());
        }
    }

    //ПАРТИИ

    private void handleBatchAdd(List<String> args) {
        if (args.size() < 6) {
            System.out.println("Ошибка использования.");
            System.out.println("Пример: batch_add 1 \"Sigma-Aldrich банка\" 500 G \"Шкаф 1\"");
            return;
        }

        long reagentId = Long.parseLong(args.get(1));
        if (reagentRepo.findById(reagentId) == null) {
            System.out.println("Ошибка: Реагент с ID " + reagentId + " не найден в базе!");
            return;
        }

        ReagentBatch batch = new ReagentBatch();
        batch.setReagentId(reagentId);
        batch.setLabel(args.get(2));
        batch.setQuantityCurrent(Double.parseDouble(args.get(3)));
        batch.setUnit(BatchUnit.valueOf(args.get(4).toUpperCase()));
        batch.setLocation(args.get(5));
        batch.setStatus(BatchStatus.ACTIVE);
        batch.setCreatedAt(Instant.now());
        batch.setUpdatedAt(Instant.now());

        batchRepo.save(batch);
        System.out.println("Партия добавлена на склад: [ID: " + batch.getId() + "] Этикетка: '" + batch.getLabel() + "'");
    }

    private void handleBatchList() {
        List<ReagentBatch> batches = batchRepo.findAll();
        if (batches.isEmpty()) {
            System.out.println("Список партий пуст.");
            return;
        }
        System.out.println("СПИСОК ПАРТИЙ НА СКЛАДЕ:");
        for (ReagentBatch b : batches) {
            String statusIcon = (b.getStatus() == BatchStatus.ACTIVE) ? "в наличии" : "отсутствуют";
            System.out.printf(" %s [%d] Реагент ID: %d | '%s' | Остаток: %.2f %s | Место: %s\n",
                    statusIcon, b.getId(), b.getReagentId(), b.getLabel(), b.getQuantityCurrent(), b.getUnit(), b.getLocation());
        }
    }

    private void handleBatchShow(List<String> args) {
        if (args.size() < 2) {
            System.out.println("Укажите ID партии. Пример: batch_show 1");
            return;
        }
        long batchId = Long.parseLong(args.get(1));
        ReagentBatch b = batchRepo.findById(batchId);
        if (b == null) {
            System.out.println("Партия с таким ID не найдена.");
            return;
        }

        Reagent r = reagentRepo.findById(b.getReagentId());
        String rName = (r != null) ? r.getName() : "Неизвестный реагент";
        String statusIcon = (b.getStatus() == BatchStatus.ACTIVE) ? "ACTIVE" : "ARCHIVED";

        System.out.println("\n ДЕТАЛИ ПАРТИИ #" + b.getId());
        System.out.println(" Реагент:  " + rName + " (ID: " + b.getReagentId() + ")");
        System.out.println(" Этикетка: " + b.getLabel());
        System.out.println(" Остаток:  " + b.getQuantityCurrent() + " " + b.getUnit());
        System.out.println(" Локация:  " + b.getLocation());
        System.out.println(" Статус:   " + statusIcon);
        System.out.println(" Создана:  " + b.getCreatedAt());
    }

    private void handleBatchUpdate(List<String> args) {
        if (args.size() < 3) {
            System.out.println("Укажите ID партии и новый остаток. Пример: batch_update 1 450");
            return;
        }
        long batchId = Long.parseLong(args.get(1));
        double newQty = Double.parseDouble(args.get(2));

        ReagentBatch b = batchRepo.findById(batchId);
        if (b == null) {
            System.out.println("Партия не найдена.");
            return;
        }

        b.setQuantityCurrent(newQty);
        b.setUpdatedAt(Instant.now());
        batchRepo.save(b);
        System.out.println("Остаток успешно обновлен! Текущий остаток: " + newQty + " " + b.getUnit());
    }

    private void handleBatchArchive(List<String> args) {
        if (args.size() < 2) {
            System.out.println("Укажите ID партии. Пример: batch_archive 1");
            return;
        }
        long batchId = Long.parseLong(args.get(1));
        ReagentBatch b = batchRepo.findById(batchId);
        if (b == null) {
            System.out.println("Партия не найдена.");
            return;
        }

        b.setStatus(BatchStatus.ARCHIVED);
        b.setUpdatedAt(Instant.now());
        batchRepo.save(b);
        System.out.println("Партия #" + b.getId() + " успешно списана и перенесена в АРХИВ.");
    }

    // ОТЧЕТЫ

    private void handleStockReport() {
        System.out.println("\n === СВОДКА ПО СКЛАДУ ===");
        List<Reagent> reagents = reagentRepo.findAll();

        if (reagents.isEmpty()) {
            System.out.println("Склад абсолютно пуст. Добавьте реагенты и партии.");
            return;
        }

        for (Reagent r : reagents) {
            List<ReagentBatch> rBatches = batchRepo.findAllByReagentId(r.getId());
            long activeCount = rBatches.stream().filter(b -> b.getStatus() == BatchStatus.ACTIVE).count();

            if (activeCount > 0) {
                System.out.printf(" %s (ID: %d) — Активных партий: %d шт.\n", r.getName(), r.getId(), activeCount);
            } else {
                System.out.printf(" %s (ID: %d) — Нет в наличии (0 активных партий)\n", r.getName(), r.getId());
            }
        }
        System.out.println("===========================");
    }
}