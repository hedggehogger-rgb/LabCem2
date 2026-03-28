package ru.Meowteam.lab.cli.command;

import ru.Meowteam.lab.domain.Reagent;
import ru.Meowteam.lab.domain.ReagentBatch;
import ru.Meowteam.lab.exception.ValidationException;
import ru.Meowteam.lab.service.BatchService;
import ru.Meowteam.lab.service.ReagentService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class StockReportCommand implements Command {
    private final ReagentService reagentService;
    private final BatchService batchService;

    public StockReportCommand(ReagentService reagentService, BatchService batchService) {
        this.reagentService = reagentService;
        this.batchService = batchService;
    }

    @Override
    public String getName() { return "stock_report"; }

    @Override
    public String getDescription() { return "Показать сводку по складу"; }

    @Override
    public void execute(List<String> args, Scanner scanner) {
        Instant expiresBefore = null;

        if (args.size() >= 3 && args.get(1).equals("--expires-before")) {
            try {
                expiresBefore = LocalDate.parse(args.get(2)).atStartOfDay(ZoneId.systemDefault()).toInstant();
            } catch (DateTimeParseException e) {
                throw new ValidationException("дата должна быть YYYY-MM-DD");
            }
        }

        List<Reagent> reagents = reagentService.searchReagents(null);
        System.out.println("Batch  Label                Reagent              Expires");

        for (Reagent r : reagents) {
            List<ReagentBatch> activeBatches = batchService.listBatches(r.getId(), true);

            if (expiresBefore != null) {
                final Instant threshold = expiresBefore;
                activeBatches = activeBatches.stream()
                        .filter(b -> b.getExpiresAt() != null && b.getExpiresAt().isBefore(threshold))
                        .collect(Collectors.toList());
            }

            for (ReagentBatch b : activeBatches) {
                String expStr = b.getExpiresAt() != null ? LocalDate.ofInstant(b.getExpiresAt(), ZoneId.systemDefault()).toString() : "-";
                System.out.printf("%-6d %-20s %-20s %s\n", b.getId(), b.getLabel(), r.getName(), expStr);
            }
        }
    }
}