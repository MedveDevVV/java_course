package autoservice.ui.actions;

import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class ImportMastersAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(ImportMastersAction.class);
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public ImportMastersAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения: импорт мастеров из CSV");

        try {
            System.out.println("\nИмпорт мастеров из CSV:");
            System.out.print("Введите путь к файлу: ");
            String filePath = scanner.nextLine();

            logger.info("Импорт мастеров из файла: {}", filePath);

            admin.importMastersFromCsv(Paths.get(filePath));

            logger.info("Команда 'импорт мастеров из CSV' успешно выполнена. Файл: {}", filePath);
            System.out.println("Мастера успешно импортированы!");

        } catch (IOException e) {
            logger.error("Ошибка выполнения команды 'импорт мастеров из CSV': ошибка чтения файла", e);
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка выполнения команды 'импорт мастеров из CSV': ошибка формата данных в CSV", e);
            System.out.println("Ошибка формата данных в CSV: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка выполнения команды 'импорт мастеров из CSV'", e);
            System.out.println("Неизвестная ошибка при импорте: " + e.getMessage());
        }
    }
}