package autoservice.ui.actions;

import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;

import java.nio.file.Paths;
import java.util.Scanner;

public class ImportPlacesAction implements IAction {
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public ImportPlacesAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\nИмпорт рабочих мест из CSV:");
        System.out.print("Введите путь к файлу: ");
        String filePath = scanner.nextLine();

        try {
            admin.importPlacesFromCsv(Paths.get(filePath));
            System.out.println("Рабочие места успешно импортированы!");
        } catch (Exception e) {
            System.out.println("Ошибка при импорте: " + e.getMessage());
        }
    }
}