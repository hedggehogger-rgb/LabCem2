package ru.Meowteam.lab.cli.command;

import ru.Meowteam.lab.domain.ReagentBatch;
import ru.Meowteam.lab.exception.ValidationException;
import ru.Meowteam.lab.service.BatchService;

import java.util.List;
import java.util.Scanner;

public class BatchListCommand implements Command {
    private final BatchService batchService;

    public BatchListCommand(BatchService batchService) {
        this.batchService = batchService;
    }

    @Override
    public String getName() { return "batch_list"; }

    @Override
    public String getDescription() { return "Показать список всех партий"; }

    @Override
    public void execute(List<String> args, Scanner scanner) {
        if (args.size() < 2) {
            throw new ValidationException("Укажите ID реагента. Пример: batch_list 10");
        }

        long reagentId;
        try {
            reagentId = Long.parseLong(args.get(1));
        } catch (NumberFormatException e) {
            throw new ValidationException("reagent_id должен быть числом");
        }

        boolean activeOnly = args.contains("--active");
        List<ReagentBatch> batches = batchService.listBatches(reagentId, activeOnly);

        System.out.println("ID  Label                Qty      Unit  Location     Status");
        for (ReagentBatch b : batches) {
            System.out.printf("%-3d %-20s %-8.2f %-5s %-12s %s\n",
                    b.getId(), b.getLabel(), b.getQuantityCurrent(), b.getUnit(), b.getLocation(), b.getStatus());
        }
    }
}