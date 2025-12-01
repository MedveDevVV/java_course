package autoservice.ui.actions;

import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;

import java.nio.file.Paths;
import java.util.Scanner;

public class ImportMastersAction implements IAction {
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public ImportMastersAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\nИмпорт мастеров из CSV:");
        System.out.print("Введите путь к файлу: ");
        String filePath = scanner.nextLine();

        try {
            admin.importMastersFromCsv(Paths.get(filePath));
            System.out.println("Мастера успешно импортированы!");
        } catch (Exception e) {
            System.out.println("Ошибка при импорте: " + e.getMessage());
        }
    }
}