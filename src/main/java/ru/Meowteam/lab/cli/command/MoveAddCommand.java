package ru.Meowteam.lab.cli.command;

import ru.Meowteam.lab.domain.StockMove;
import ru.Meowteam.lab.domain.StockMoveType;
import ru.Meowteam.lab.exception.ValidationException;
import ru.Meowteam.lab.service.MoveService;

import java.util.List;
import java.util.Scanner;

public class MoveAddCommand implements Command {
    private final MoveService moveService;

    public MoveAddCommand(MoveService moveService) {
        this.moveService = moveService;
    }

    @Override
    public String getName() { return "move_add <ID_Партии>"; }

    @Override
    public String getDescription() { return "Оформить приход/расход/списание"; }

    @Override
    public void execute(List<String> args, Scanner scanner) {
        if (args.size() < 2) throw new ValidationException("Укажите ID бутылки. Пример: move_add 77");

        long batchId;
        try {
            batchId = Long.parseLong(args.get(1));
        } catch (NumberFormatException e) {
            throw new ValidationException("batch_id должен быть числом");
        }

        System.out.print("Тип (IN/OUT/DISCARD): ");
        StockMoveType type;
        try {
            type = StockMoveType.valueOf(scanner.nextLine().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("тип только IN, OUT или DISCARD");
        }

        System.out.print("Количество: ");
        double quantity;
        try {
            quantity = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("количество должно быть числом");
        }

        System.out.print("Причина (можно пусто): ");
        String reason = scanner.nextLine().trim();

        StockMove move = moveService.makeMove(batchId, type, quantity, reason);
        System.out.println("OK move_id=" + move.getId());
    }
}