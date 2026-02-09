package autoservice.ui.actions;

import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Optional;

public class FindAvailableDateAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(FindAvailableDateAction.class);
    private final AutoServiceAdmin admin;

    public FindAvailableDateAction(AutoServiceAdmin admin) {
        this.admin = admin;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения: поиск ближайшей свободной даты");

        try {
            System.out.println("\nПоиск ближайшей свободной даты:");

            Optional<LocalDate> availableDate = admin.getFirstAvailableSlot(LocalDate.now());

            if (availableDate.isPresent()) {
                logger.info("Найдена свободная дата: {}", availableDate.get());
                logger.info("Команда 'поиск ближайшей свободной даты' успешно выполнена. Дата: {}",
                        availableDate.get());
                System.out.println("Ближайшая свободная дата: " + availableDate.get());
            } else {
                logger.info("Свободных дат не найдено на ближайшую неделю");
                logger.info("Команда 'поиск ближайшей свободной даты' выполнена (результат: дат нет)");
                System.out.println("Нет свободных дат в ближайшую неделю!");
            }

        } catch (Exception e) {
            logger.error("Ошибка выполнения команды 'поиск ближайшей свободной даты'", e);
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}