package ru.Meowteam.lab.cli.command;

import ru.Meowteam.lab.domain.Reagent;
import ru.Meowteam.lab.domain.ReagentBatch;
import ru.Meowteam.lab.exception.ValidationException;
import ru.Meowteam.lab.service.BatchService;
import ru.Meowteam.lab.service.ReagentService;

import java.util.List;
import java.util.Scanner;

public class BatchShowCommand implements Command {
    private final BatchService batchService;
    private final ReagentService reagentService;

    public BatchShowCommand(BatchService batchService, ReagentService reagentService) {
        this.batchService = batchService;
        this.reagentService = reagentService;
    }

    @Override
    public String getName() { return "batch_show <ID_Партии>"; }

    @Override
    public String getDescription() { return "Подробная информация о партии"; }

    @Override
    public void execute(List<String> args, Scanner scanner) {
        if (args.size() < 2) throw new ValidationException("Укажите ID партии. Пример: batch_show 77");

        long batchId;
        try {
            batchId = Long.parseLong(args.get(1));
        } catch (NumberFormatException e) {
            throw new ValidationException("batch_id должен быть числом");
        }

        ReagentBatch b = batchService.getById(batchId);
        Reagent r = reagentService.getById(b.getReagentId());

        System.out.println("Batch #" + b.getId());
        System.out.println("reagent: " + r.getName());
        System.out.println("label: " + b.getLabel());
        System.out.println("qty_current: " + b.getQuantityCurrent() + " " + b.getUnit().name().toLowerCase());
        System.out.println("location: " + b.getLocation());
        System.out.println("status: " + b.getStatus());
        if (b.getExpiresAt() != null) {
            System.out.println("expires: " + b.getExpiresAt());
        }
    }
}