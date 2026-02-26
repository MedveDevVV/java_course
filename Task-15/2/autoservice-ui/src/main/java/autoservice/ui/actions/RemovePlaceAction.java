package autoservice.ui.actions;

import autoservice.exception.OperationNotAllowedException;
import autoservice.model.WorkshopPlace;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import autoservice.utils.csv.InputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class RemovePlaceAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(RemovePlaceAction.class);
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public RemovePlaceAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения: удаление рабочего места");

        try {
            System.out.println("\nУдаление рабочего места:");
            List<WorkshopPlace> places = admin.getAvailablePlaces(LocalDate.now());

            if (places.isEmpty()) {
                logger.warn("Попытка удаления рабочего места: нет доступных мест");
                System.out.println("Нет доступных рабочих мест!");
                return;
            }

            System.out.println("Список рабочих мест:");
            for (int i = 0; i < places.size(); i++) {
                System.out.println((i + 1) + ". " + places.get(i).getName());
            }

            int choice = InputUtils.readNumberInRange(
                    scanner, "Выберите номер места для удаления: ", 0, places.size());

            WorkshopPlace placeToRemove = places.get(choice - 1);
            logger.info("Удаление рабочего места: {} (ID: {})",
                    placeToRemove.getName(), placeToRemove.getId());

            admin.removePlace(placeToRemove);

            logger.info("Команда 'удаление рабочего места' успешно выполнена. Место: {} (ID: {})",
                    placeToRemove.getName(), placeToRemove.getId());
            System.out.println("Рабочее место удалено!");
        } catch (OperationNotAllowedException e) {
            logger.error("Ошибка выполнения команды 'удаление рабочего места': операция запрещена", e);
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка выполнения команды 'удаление рабочего места'", e);
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}