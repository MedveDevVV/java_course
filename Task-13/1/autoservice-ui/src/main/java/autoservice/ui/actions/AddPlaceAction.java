package autoservice.ui.actions;

import autoservice.exception.OperationNotAllowedException;
import autoservice.model.WorkshopPlace;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class AddPlaceAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(AddPlaceAction.class);
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public AddPlaceAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения: добавление рабочего места");

        try {
            System.out.println("\nДобавление рабочего места:");
            System.out.print("Название места: ");
            String name = scanner.nextLine();

            admin.addWorkshopPlace(new WorkshopPlace(name));

            logger.info("Команда 'добавление рабочего места' успешно выполнена. Место: {}", name);
            System.out.println("Рабочее место добавлено!");
        } catch (OperationNotAllowedException e) {
            logger.error("Ошибка выполнения команды 'добавление рабочего места': {}", e.getMessage());
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка выполнения команды 'добавление рабочего места'", e);
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}