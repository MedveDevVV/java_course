package autoservice.ui.actions;

import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Scanner;

public class ExportOrdersAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(ExportOrdersAction.class);
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public ExportOrdersAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения: экспорт заказов в CSV");

        try {
            System.out.println("\nЭкспорт заказов в CSV:");
            System.out.print("Введите путь к файлу: ");
            String filePath = scanner.nextLine();

            logger.info("Экспорт заказов в файл: {}", filePath);

            admin.exportOrdersToCsv(Paths.get(filePath));

            logger.info("Команда 'экспорт заказов в CSV' успешно выполнена. Файл: {}", filePath);
            System.out.println("Заказы успешно экспортированы!");
        } catch (Exception e) {
            logger.error("Ошибка выполнения команды 'экспорт заказов в CSV'", e);
            System.out.println("Ошибка при экспорте: " + e.getMessage());
        }
    }
}