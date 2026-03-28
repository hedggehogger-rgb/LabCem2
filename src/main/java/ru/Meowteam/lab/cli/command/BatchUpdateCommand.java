package ru.Meowteam.lab.cli.command;

import ru.Meowteam.lab.exception.ValidationException;
import ru.Meowteam.lab.service.BatchService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Scanner;

public class BatchUpdateCommand implements Command {
    private final BatchService batchService;

    public BatchUpdateCommand(BatchService batchService) {
        this.batchService = batchService;
    }

    @Override
    public String getName() { return "batch_update <ID_Партии>"; }

    @Override
    public String getDescription() { return "Изменить данные о партии"; }

    @Override
    public void execute(List<String> args, Scanner scanner) {
        if (args.size() < 3) throw new ValidationException("Укажите ID и поля. Пример: batch_update 77 location=Shelf-2");

        long batchId;
        try {
            batchId = Long.parseLong(args.get(1));
        } catch (NumberFormatException e) {
            throw new ValidationException("batch_id должен быть числом");
        }

        for (int i = 2; i < args.size(); i++) {
            String[] parts = args.get(i).split("=", 2);
            if (parts.length < 2) throw new ValidationException("Ожидается формат field=value для '" + args.get(i) + "'");

            String field = parts[0];
            String value = parts[1];
            Instant parsedDate = null;

            if (field.equalsIgnoreCase("expiresAt")) {
                try {
                    parsedDate = LocalDate.parse(value).atStartOfDay(ZoneId.systemDefault()).toInstant();
                } catch (Exception e) {
                    throw new ValidationException("Неверная дата для expiresAt, используйте YYYY-MM-DD");
                }
            }
            batchService.updateBatch(batchId, field, value, parsedDate);
        }
        System.out.println("OK");
    }
}