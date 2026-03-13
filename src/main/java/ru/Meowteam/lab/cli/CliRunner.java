package ru.Meowteam.lab.cli;

import ru.Meowteam.lab.domain.*;
import ru.Meowteam.lab.exception.ValidationException;
import ru.Meowteam.lab.repository.*;
import ru.Meowteam.lab.service.*;
import ru.Meowteam.lab.validation.*;

import java.util.List;
import java.util.Scanner;

public class CliRunner {

    // Репозитории
    private final ReagentRepository reagentRepo = new ReagentRepository();
    private final BatchRepository batchRepo = new BatchRepository();
    private final MoveRepository moveRepo = new MoveRepository();

    // Валидаторы
    private final ReagentValidator reagentValidator = new ReagentValidator();
    private final BatchValidator batchValidator = new BatchValidator();
    private final MoveValidator moveValidator = new MoveValidator();

    // Сервисы
    private final ReagentService reagentService = new ReagentService(reagentRepo, reagentValidator);
    private final BatchService batchService = new BatchService(batchRepo, batchValidator, reagentService);
    private final MoveService moveService = new MoveService(moveRepo, moveValidator, batchService, batchRepo);

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

                    // Команды Реагентов
                    case "reag_add": handleReagAdd(args); break;
                    case "reag_list": handleReagList(); break;

                    // Команды Партий
                    case "batch_add": handleBatchAdd(args); break;
                    case "batch_list": handleBatchList(); break;
                    case "batch_show": handleBatchShow(args); break;
                    case "batch_update": handleBatchUpdate(args); break;
                    case "batch_archive": handleBatchArchive(args); break;

                    // --- Движение по складу ---
                    case "move": handleMove(args); break;
                    case "move_list": handleMoveList(args); break;

                    // Отчеты
                    case "stock_report": handleStockReport(); break;

                    default:
                        System.out.println("Неизвестная команда: '" + command + "'. Введите 'help' для вывода меню.");
                        break;
                }
            } catch (ValidationException e) {
                System.out.println("ОШИБКА ВАЛИДАЦИИ: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("СИСТЕМНАЯ ОШИБКА: Проверьте правильность введенных данных.");
                System.out.println("Детали: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private void printHelp() {
        System.out.println("\n === СПИСОК ДОСТУПНЫХ КОМАНД === ");
        System.out.println("\n РЕАГЕНТЫ:");
        System.out.println("  reag_add \"Название\" [\"Формула\"] [\"CAS\"] [\"Класс опасности\"] - Добавить новый реагент");
        System.out.println("  reag_list                                                   - Показать список всех реагентов");

        System.out.println("\n ПАРТИИ:");
        System.out.println("  batch_add <ID_Реагента> \"Этикетка\" <Кол-во> <G|ML> \"Место\" - Зарегистрировать новую партию");
        System.out.println("  batch_list                                                   - Показать список всех партий");
        System.out.println("  batch_show <ID_Партии>                                       - Подробная информация о партии");
        System.out.println("  batch_update <ID_Партии> <Поле: label|location|status> \"Значение\" - Изменить данные о партии");
        System.out.println("  batch_archive <ID_Партии>                                    - Отправить партию в архив");

        System.out.println("\n УЧЕТ ОСТАТКОВ:");
        System.out.println("  move_add <ID_Партии> <IN|OUT|DISCARD> <Кол-во> [\"Причина\"]     - Оформить приход/расход/списание");
        System.out.println("  move_list <ID_Партии> [Лимит]                                - История движений по партии");

        System.out.println("\n ОТЧЕТЫ:");
        System.out.println("  stock_report                                                 - Показать сводку по складу");

        System.out.println("\n ПРОЧЕЕ:");
        System.out.println("  help, exit\n");
    }

    // РЕАГЕНТЫ

    private void handleReagAdd(List<String> args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Название: ");
        String name = scanner.nextLine().trim();

        System.out.print("Формула (можно пусто): ");
        String formula = scanner.nextLine().trim();

        System.out.print("CAS (можно пусто): ");
        String cas = scanner.nextLine().trim();

        System.out.print("Класс опасности (можно пусто): ");
        String hazardClass = scanner.nextLine().trim();

        Reagent reagent = reagentService.createReagent(name, formula, cas, hazardClass);
        System.out.println("OK reagent_id=" + reagent.getId());
    }

    private void handleReagList() {
        List<Reagent> reagents = reagentService.searchReagents(null);
        if (reagents.isEmpty()) {
            System.out.println("Справочник реагентов пока пуст.");
            return;
        }
        System.out.println("\n СПИСОК РЕАГЕНТОВ:");
        for (Reagent r : reagents) {
            System.out.printf("[%d] %s | Формула: %s | CAS: %s\n",
                    r.getId(), r.getName(), r.getFormula(), r.getCas());
        }
    }

    // ПАРТИИ

    private void handleBatchAdd(List<String> args) {
        if (args.size() < 6) {
            System.out.println("Пример: batch_add 1 \"Sigma-Aldrich банка\" 500 G \"Шкаф 1\"");
            return;
        }

        long reagentId = Long.parseLong(args.get(1));
        String label = args.get(2);
        double quantity = Double.parseDouble(args.get(3));
        BatchUnit unit = BatchUnit.valueOf(args.get(4).toUpperCase());
        String location = args.get(5);

        ReagentBatch batch = batchService.createBatch(reagentId, label, quantity, unit, location, null);
        System.out.println("Партия добавлена на склад: [ID: " + batch.getId() + "] Этикетка: '" + batch.getLabel() + "'");
    }

    private void handleBatchList() {
        List<ReagentBatch> batches = batchService.getAllBatches();
        if (batches.isEmpty()) {
            System.out.println("Список партий пуст.");
            return;
        }
        System.out.println("СПИСОК ПАРТИЙ НА СКЛАДЕ:");
        for (ReagentBatch b : batches) {
            String statusIcon = (b.getStatus() == BatchStatus.ACTIVE) ? "В наличии" : "В архиве";
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
        ReagentBatch b = batchService.getById(batchId);
        Reagent r = reagentService.getById(b.getReagentId());

        System.out.println("\n ДЕТАЛИ ПАРТИИ #" + b.getId());
        System.out.println(" Реагент:  " + r.getName() + " (ID: " + b.getReagentId() + ")");
        System.out.println(" Этикетка: " + b.getLabel());
        System.out.println(" Остаток:  " + b.getQuantityCurrent() + " " + b.getUnit());
        System.out.println(" Локация:  " + b.getLocation());
        System.out.println(" Статус:   " + b.getStatus());
        System.out.println(" Создана:  " + b.getCreatedAt());
        System.out.println(" Обновлена:" + b.getUpdatedAt());
    }

    private void handleBatchUpdate(List<String> args) {
        if (args.size() < 4) {
            System.out.println("Пример: batch_update 1 location \"Холодильник 2\"");
            System.out.println("Доступные поля: location, label, status");
            return;
        }
        long batchId = Long.parseLong(args.get(1));
        String field = args.get(2);
        String value = args.get(3);

        batchService.updateBatch(batchId, field, value, null);
        System.out.println("Партия успешно обновлена.");
    }

    private void handleBatchArchive(List<String> args) {
        if (args.size() < 2) {
            System.out.println("Пример: batch_archive 1");
            return;
        }
        long batchId = Long.parseLong(args.get(1));
        batchService.archiveBatch(batchId);
        System.out.println("Партия #" + batchId + " успешно отправлена в АРХИВ.");
    }

    //ДВИЖЕНИЕ ОСТАТКОВ

    private void handleMove(List<String> args) {
        if (args.size() < 4) {
            System.out.println("Пример: move 1 OUT 50 \"Взвешивание для опыта\"");
            System.out.println("Типы движений: IN (Приход), OUT (Расход), DISCARD (Списание)");
            return;
        }
        long batchId = Long.parseLong(args.get(1));
        StockMoveType type = StockMoveType.valueOf(args.get(2).toUpperCase());
        double quantity = Double.parseDouble(args.get(3));
        String reason = args.size() > 4 ? args.get(4) : "Без причины";

        StockMove move = moveService.makeMove(batchId, type, quantity, reason);
        System.out.printf("Движение успешно записано! [Move ID: %d] Тип: %s, Кол-во: %.2f %s\n",
                move.getId(), move.getType(), move.getQuantity(), move.getUnit());
    }

    private void handleMoveList(List<String> args) {
        if (args.size() < 2) {
            System.out.println("Укажите ID партии. Пример: move_list 1");
            return;
        }
        long batchId = Long.parseLong(args.get(1));
        int limit = args.size() > 2 ? Integer.parseInt(args.get(2)) : 10;

        List<StockMove> moves = moveService.listMoves(batchId, limit);
        if (moves.isEmpty()) {
            System.out.println("Движений по данной партии не найдено.");
            return;
        }

        System.out.println("\n ИСТОРИЯ ДВИЖЕНИЙ ПО ПАРТИИ #" + batchId + ":");
        for (StockMove m : moves) {
            System.out.printf(" [%s] %s %.2f %s | Причина: %s\n",
                    m.getMovedAt(), m.getType(), m.getQuantity(), m.getUnit(), m.getReason());
        }
    }

    // ОТЧЕТЫ

    private void handleStockReport() {
        System.out.println("\n === СВОДКА ПО СКЛАДУ ===");
        List<Reagent> reagents = reagentService.searchReagents(null);

        if (reagents.isEmpty()) {
            System.out.println("Склад пуст. Добавьте реагенты и партии.");
            return;
        }

        for (Reagent r : reagents) {
            List<ReagentBatch> activeBatches = batchService.listBatches(r.getId(), true);

            if (!activeBatches.isEmpty()) {
                double totalAmount = activeBatches.stream().mapToDouble(ReagentBatch::getQuantityCurrent).sum();
                System.out.printf(" %s (ID: %d) — Активных партий: %d шт. (Общий остаток: %.2f)\n",
                        r.getName(), r.getId(), activeBatches.size(), totalAmount);
            } else {
                System.out.printf(" %s (ID: %d) — Нет в наличии\n", r.getName(), r.getId());
            }
        }
    }
}