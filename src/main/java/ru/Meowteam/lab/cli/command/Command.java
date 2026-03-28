package ru.Meowteam.lab.cli.command;

import java.util.List;
import java.util.Scanner;

public interface Command {
    // Имя команды, которое пользователь вводит в консоль (например, "reag_add")
    String getName();

    // Описание для команды Help
    String getDescription();

    // Сама логика выполнения
    void execute(List<String> args, Scanner scanner);
}