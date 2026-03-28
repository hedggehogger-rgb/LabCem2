package ru.Meowteam.lab.cli;

import ru.Meowteam.lab.cli.command.*;
import ru.Meowteam.lab.exception.ExitException;
import ru.Meowteam.lab.exception.ValidationException;
import ru.Meowteam.lab.repository.*;
import ru.Meowteam.lab.service.*;
import ru.Meowteam.lab.validation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CliRunner {
    private final ReagentRepository reagentRepo = new ReagentRepository();
    private final BatchRepository batchRepo = new BatchRepository();
    private final MoveRepository moveRepo = new MoveRepository();

    private final ReagentValidator reagentValidator = new ReagentValidator();
    private final BatchValidator batchValidator = new BatchValidator();
    private final MoveValidator moveValidator = new MoveValidator();

    private final ReagentService reagentService = new ReagentService(reagentRepo, reagentValidator);
    private final BatchService batchService = new BatchService(batchRepo, batchValidator, reagentService);
    private final MoveService moveService = new MoveService(moveRepo, moveValidator, batchService, batchRepo);

    // Мапа для хранения всех доступных команд
    private final Map<String, Command> commands = new LinkedHashMap<>();

    public CliRunner() {
        // Реактивы
        registerCommand(new ReagAddCommand(reagentService));
        registerCommand(new ReagListCommand(reagentService));

        // Партии (Бутылки)
        registerCommand(new BatchAddCommand(batchService));
        registerCommand(new BatchListCommand(batchService));
        registerCommand(new BatchShowCommand(batchService, reagentService));
        registerCommand(new BatchUpdateCommand(batchService));
        registerCommand(new BatchArchiveCommand(batchService));

        // Движение остатков
        registerCommand(new MoveAddCommand(moveService));
        registerCommand(new MoveListCommand(moveService));

        // Отчеты
        registerCommand(new StockReportCommand(reagentService, batchService));

        // Системные
        registerCommand(new HelpCommand(commands));
        registerCommand(new ExitCommand());
    }

    private void registerCommand(Command command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=========================================");
        System.out.println(" Добро пожаловать в Laboratory System! ");
        System.out.println(" Введите 'help' для списка команд. ");
        System.out.println("=========================================");

        while (true) {
            System.out.print("\n> ");
            String inputLine = scanner.nextLine().trim();

            if (inputLine.isEmpty()) continue;

            List<String> args = CommandParser.parseArgs(inputLine);
            String commandName = args.get(0).toLowerCase();

            Command command = commands.get(commandName);

            if (command == null) {
                System.out.println("Ошибка: Неизвестная команда '" + commandName + "'. Введите 'help' для списка.");
                continue;
            }

            try {
                command.execute(args, scanner);
            } catch (ExitException e) {
                System.out.println("Завершение работы.");
                break;
            } catch (ValidationException e) {
                System.out.println("ОШИБКА: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("СИСТЕМНАЯ ОШИБКА: Проверьте правильность введенных данных.");
                System.out.println("Детали: " + e.getMessage());
            }
        }
        scanner.close();
    }
}