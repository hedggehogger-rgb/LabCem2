package ru.Meowteam.lab.cli.command;

import ru.Meowteam.lab.domain.Reagent;
import ru.Meowteam.lab.service.ReagentService;

import java.util.List;
import java.util.Scanner;

public class ReagAddCommand implements Command {

    // Команде нужен сервис для работы с реагентами
    private final ReagentService reagentService;

    public ReagAddCommand(ReagentService reagentService) {
        this.reagentService = reagentService;
    }

    @Override
    public String getName() {
        return "reag_add";
    }

    @Override
    public String getDescription() {
        return "Добавить новый реактив";
    }

    @Override
    public void execute(List<String> args, Scanner scanner) {
        System.out.print("Название: ");
        String name = scanner.nextLine().trim();

        System.out.print("Формула (можно пусто): ");
        String formula = scanner.nextLine().trim();

        System.out.print("CAS (можно пусто): ");
        String cas = scanner.nextLine().trim();

        System.out.print("Класс опасности (можно пусто): ");
        String hazardClass = scanner.nextLine().trim();

        Reagent reagent = reagentService.createReagent(name, formula, cas, hazardClass);
        System.out.println("OK reagent_id=" + reagent.getId());
    }
}