package autoservice.ui.actions;

import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Scanner;

public class ImportOrdersAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(ImportOrdersAction.class);
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public ImportOrdersAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения: импорт заказов из CSV");

        try {
            System.out.println("\nИмпорт заказов из CSV:");
            System.out.print("Введите путь к файлу: ");
            String filePath = scanner.nextLine();

            logger.info("Импорт заказов из файла: {}", filePath);

            admin.importOrdersFromCsv(Paths.get(filePath));

            logger.info("Команда 'импорт заказов из CSV' успешно выполнена. Файл: {}", filePath);
            System.out.println("Заказы успешно импортированы!");
        } catch (Exception e) {
            logger.error("Ошибка выполнения команды 'импорт заказов из CSV'", e);
            System.out.println("Ошибка при импорте: " + e.getMessage());
        }
    }
}