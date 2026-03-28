package ru.Meowteam.lab.cli.command;

import ru.Meowteam.lab.domain.StockMove;
import ru.Meowteam.lab.exception.ValidationException;
import ru.Meowteam.lab.service.MoveService;

import java.util.List;
import java.util.Scanner;

public class MoveListCommand implements Command {
    private final MoveService moveService;

    public MoveListCommand(MoveService moveService) {
        this.moveService = moveService;
    }

    @Override
    public String getName() { return "move_list <ID_Партии>"; }

    @Override
    public String getDescription() { return "История движений по партии"; }

    @Override
    public void execute(List<String> args, Scanner scanner) {
        if (args.size() < 2) throw new ValidationException("Укажите ID. Пример: move_list 77");

        long batchId;
        try {
            batchId = Long.parseLong(args.get(1));
        } catch (NumberFormatException e) {
            throw new ValidationException("batch_id должен быть числом");
        }

        int limit = 0;
        if (args.size() >= 4 && args.get(2).equals("--last")) {
            try {
                limit = Integer.parseInt(args.get(3));
            } catch (NumberFormatException e) {
                throw new ValidationException("лимит должен быть числом");
            }
        }

        List<StockMove> moves = moveService.listMoves(batchId, limit);
        System.out.println("ID   Type      Qty      Unit  Reason");
        for (StockMove m : moves) {
            String reason = m.getReason() == null ? "-" : m.getReason();
            System.out.printf("%-4d %-9s %-8.2f %-5s %s\n",
                    m.getId(), m.getType(), m.getQuantity(), m.getUnit(), reason);
        }
    }
}