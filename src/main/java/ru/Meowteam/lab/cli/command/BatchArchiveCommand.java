package ru.Meowteam.lab.cli.command;

import ru.Meowteam.lab.exception.ValidationException;
import ru.Meowteam.lab.service.BatchService;

import java.util.List;
import java.util.Scanner;

public class BatchArchiveCommand implements Command {
    private final BatchService batchService;

    public BatchArchiveCommand(BatchService batchService) {
        this.batchService = batchService;
    }

    @Override
    public String getName() { return "batch_archive <ID_Партии>"; }

    @Override
    public String getDescription() { return "Отправить партию в архив"; }

    @Override
    public void execute(List<String> args, Scanner scanner) {
        if (args.size() < 2) throw new ValidationException("Укажите ID. Пример: batch_archive 77");
        try {
            long batchId = Long.parseLong(args.get(1));
            batchService.archiveBatch(batchId);
            System.out.println("OK batch " + batchId + " archived");
        } catch (NumberFormatException e) {
            throw new ValidationException("batch_id должен быть числом");
        }
    }
}