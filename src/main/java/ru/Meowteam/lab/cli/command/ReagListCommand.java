package ru.Meowteam.lab.cli.command;

import ru.Meowteam.lab.domain.Reagent;
import ru.Meowteam.lab.service.ReagentService;

import java.util.List;
import java.util.Scanner;

public class ReagListCommand implements Command {

    private final ReagentService reagentService;

    public ReagListCommand(ReagentService reagentService) {
        this.reagentService = reagentService;
    }

    @Override
    public String getName() {
        return "reag_list";
    }

    @Override
    public String getDescription() {
        return "Показать список всех реагентов";
    }

    @Override
    public void execute(List<String> args, Scanner scanner) {
        String query = null;

        // Обрабатываем аргумент --q (например: reag_list --q chloride)
        if (args.size() > 2 && args.get(1).equals("--q")) {
            query = String.join(" ", args.subList(2, args.size()));
        } else if (args.size() > 1 && !args.get(1).equals("--q")) {
            // Если ввели просто текст без --q (поддержка старого формата)
            query = String.join(" ", args.subList(1, args.size()));
        }

        List<Reagent> reagents = reagentService.searchReagents(query);

        if (reagents.isEmpty()) {
            System.out.println("Справочник реактивов пока пуст или по запросу ничего не найдено.");
            return;
        }

        System.out.println("ID  Name              Formula    CAS");
        for (Reagent r : reagents) {
            String formula = r.getFormula() == null ? "-" : r.getFormula();
            String cas = r.getCas() == null ? "-" : r.getCas();
            System.out.printf("%-3d %-17s %-10s %s\n", r.getId(), r.getName(), formula, cas);
        }
    }
}