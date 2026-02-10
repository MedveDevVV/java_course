package autoservice.ui.actions;

import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Scanner;

public class ImportPlacesAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(ImportPlacesAction.class);
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public ImportPlacesAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения: импорт рабочих мест из CSV");

        try {
            System.out.println("\nИмпорт рабочих мест из CSV:");
            System.out.print("Введите путь к файлу: ");
            String filePath = scanner.nextLine();

            logger.info("Импорт рабочих мест из файла: {}", filePath);

            admin.importPlacesFromCsv(Paths.get(filePath));

            logger.info("Команда 'импорт рабочих мест из CSV' успешно выполнена. Файл: {}", filePath);
            System.out.println("Рабочие места успешно импортированы!");

        } catch (Exception e) {
            logger.error("Ошибка выполнения команды 'импорт рабочих мест из CSV'", e);
            System.out.println("Ошибка при импорте: " + e.getMessage());
        }
    }
}