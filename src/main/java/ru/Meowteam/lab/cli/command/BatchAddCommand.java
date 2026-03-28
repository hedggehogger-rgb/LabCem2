package ru.Meowteam.lab.cli.command;

import ru.Meowteam.lab.domain.BatchUnit;
import ru.Meowteam.lab.domain.ReagentBatch;
import ru.Meowteam.lab.exception.ValidationException;
import ru.Meowteam.lab.service.BatchService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class BatchAddCommand implements Command {
    private final BatchService batchService;

    public BatchAddCommand(BatchService batchService) {
        this.batchService = batchService;
    }

    @Override
    public String getName() { return "batch_add <ID_Реагента>"; }

    @Override
    public String getDescription() { return "Зарегистрировать новую партию"; }

    @Override
    public void execute(List<String> args, Scanner scanner) {
        if (args.size() < 2) {
            throw new ValidationException("Укажите ID реагента. Пример: batch_add 10");
        }

        long reagentId;
        try {
            reagentId = Long.parseLong(args.get(1));
        } catch (NumberFormatException e) {
            throw new ValidationException("reagent_id должен быть числом");
        }

        System.out.print("Номер партии (label): ");
        String label = scanner.nextLine().trim();

        System.out.print("Начальное количество: ");
        double quantity;
        try {
            quantity = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("количество должно быть числом");
        }

        System.out.print("Единицы (g|mL): ");
        BatchUnit unit;
        try {
            unit = BatchUnit.valueOf(scanner.nextLine().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("единицы только g или mL");
        }

        System.out.print("Где хранится: ");
        String location = scanner.nextLine().trim();

        System.out.print("Годен до (YYYY-MM-DD, можно пусто): ");
        String dateStr = scanner.nextLine().trim();
        Instant expiresAt = null;
        if (!dateStr.isEmpty()) {
            try {
                expiresAt = LocalDate.parse(dateStr).atStartOfDay(ZoneId.systemDefault()).toInstant();
            } catch (DateTimeParseException e) {
                throw new ValidationException("неверный формат даты, используйте YYYY-MM-DD");
            }
        }

        ReagentBatch batch = batchService.createBatch(reagentId, label, quantity, unit, location, expiresAt);
        System.out.println("OK batch_id=" + batch.getId());
    }
}