package autoservice.ui.actions;

import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;

import java.nio.file.Paths;
import java.util.Scanner;

public class ExportMastersAction implements IAction {
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public ExportMastersAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\nЭкспорт мастеров в CSV:");
        System.out.print("Введите путь к файлу: ");
        String filePath = scanner.nextLine();

        try {
            admin.exportMastersToCsv(Paths.get(filePath));
            System.out.println("Мастера успешно экспортированы!");
        } catch (Exception e) {
            System.out.println("Ошибка при экспорте: " + e.getMessage());
        }
    }
}