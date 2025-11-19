package autoservice.ui.actions;

import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;

import java.nio.file.Paths;
import java.util.Scanner;

public class ExportPlacesAction implements IAction {
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public ExportPlacesAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\nЭкспорт рабочих мест в CSV:");
        System.out.print("Введите путь к файлу: ");
        String filePath = scanner.nextLine();

        try {
            admin.exportPlacesToCsv(Paths.get(filePath));
            System.out.println("Рабочие места успешно экспортированы!");
        } catch (Exception e) {
            System.out.println("Ошибка при экспорте: " + e.getMessage());
        }
    }
}