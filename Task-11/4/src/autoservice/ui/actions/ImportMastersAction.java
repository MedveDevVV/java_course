package autoservice.ui.actions;

import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;

import java.io.IOException;
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
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка формата данных в CSV: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Неизвестная ошибка при импорте: " + e.getMessage());
        }
    }
}