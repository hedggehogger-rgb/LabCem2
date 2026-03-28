package ru.Meowteam.lab.cli.command;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class HelpCommand implements Command {
    private final Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Показать список доступных команд";
    }

    @Override
    public void execute(List<String> args, Scanner scanner) {
        System.out.println("\n=== СПИСОК ДОСТУПНЫХ КОМАНД ===");
        for (Command cmd : commands.values()) {
            System.out.printf("  %-30s - %s\n", cmd.getName(), cmd.getDescription());
        }
    }
}