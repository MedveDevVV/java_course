package autoservice.ui.actions;

import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;

import java.nio.file.Paths;
import java.util.Scanner;

public class ExportOrdersAction implements IAction {
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public ExportOrdersAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\nЭкспорт заказов в CSV:");
        System.out.print("Введите путь к файлу: ");
        String filePath = scanner.nextLine();

        try {
            admin.exportOrdersToCsv(Paths.get(filePath));
            System.out.println("Заказы успешно экспортированы!");
        } catch (Exception e) {
            System.out.println("Ошибка при экспорте: " + e.getMessage());
        }
    }
}