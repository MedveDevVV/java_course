package autoservice.ui.actions;

import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Scanner;

public class ExportMastersAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(ExportMastersAction.class);
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public ExportMastersAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения: экспорт мастеров в CSV");

        try {
            System.out.println("\nЭкспорт мастеров в CSV:");
            System.out.print("Введите путь к файлу: ");
            String filePath = scanner.nextLine();

            logger.info("Экспорт мастеров в файл: {}", filePath);

            admin.exportMastersToCsv(Paths.get(filePath));

            logger.info("Команда 'экспорт мастеров в CSV' успешно выполнена. Файл: {}", filePath);
            System.out.println("Мастера успешно экспортированы!");

        } catch (Exception e) {
            logger.error("Ошибка выполнения команды 'экспорт мастеров в CSV'", e);
            System.out.println("Ошибка при экспорте: " + e.getMessage());
        }
    }
}