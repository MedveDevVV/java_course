package autoservice.ui.actions;

import autoservice.model.WorkshopPlace;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import autoservice.utils.csv.InputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class FindAvailablePlacesAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(FindAvailablePlacesAction.class);
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public FindAvailablePlacesAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения: поиск свободных рабочих мест");

        try {
            System.out.println("\nПоиск свободных рабочих мест:");
            LocalDate date = InputUtils.readDateInput(scanner, "Введите дату (гггг-мм-дд): ");

            logger.info("Поиск свободных рабочих мест на дату: {}", date);

            List<WorkshopPlace> places = admin.getAvailablePlaces(date);

            System.out.println("Свободные рабочие места на " + date + ":");
            if (places.isEmpty()) {
                logger.info("Свободных рабочих мест на {} не найдено", date);
                System.out.println("Нет свободных рабочих мест на указанную дату!");
            } else {
                logger.info("Найдено {} свободных рабочих мест на {}", places.size(), date);
                for (int i = 0; i < places.size(); i++) {
                    System.out.println((i + 1) + ". " + places.get(i).getName());
                }
            }

            logger.info("Команда 'поиск свободных рабочих мест' успешно выполнена. Дата: {}, найдено мест: {}",
                    date, places.size());
        } catch (Exception e) {
            logger.error("Ошибка выполнения команды 'поиск свободных рабочих мест'", e);
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}