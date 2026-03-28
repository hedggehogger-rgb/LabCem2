package ru.Meowteam.lab.cli.command;

import ru.Meowteam.lab.exception.ExitException;

import java.util.List;
import java.util.Scanner;

public class ExitCommand implements Command {
    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "Выйти из программы";
    }

    @Override
    public void execute(List<String> args, Scanner scanner) {
        throw new ExitException();
    }
}