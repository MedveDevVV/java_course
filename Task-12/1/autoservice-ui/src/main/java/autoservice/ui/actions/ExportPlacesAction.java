package autoservice.ui.actions;

import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Scanner;

public class ExportPlacesAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(ExportPlacesAction.class);
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public ExportPlacesAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения: экспорт рабочих мест в CSV");

        try {
            System.out.println("\nЭкспорт рабочих мест в CSV:");
            System.out.print("Введите путь к файлу: ");
            String filePath = scanner.nextLine();

            logger.info("Экспорт рабочих мест в файл: {}", filePath);

            admin.exportPlacesToCsv(Paths.get(filePath));

            logger.info("Команда 'экспорт рабочих мест в CSV' успешно выполнена. Файл: {}", filePath);
            System.out.println("Рабочие места успешно экспортированы!");

        } catch (Exception e) {
            logger.error("Ошибка выполнения команды 'экспорт рабочих мест в CSV'", e);
            System.out.println("Ошибка при экспорте: " + e.getMessage());
        }
    }
}