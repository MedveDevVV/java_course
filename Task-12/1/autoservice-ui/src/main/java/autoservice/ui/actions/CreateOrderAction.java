package autoservice.ui.actions;

import autoservice.dto.CarServiceMastersQuery;
import autoservice.enums.SortCarServiceMasters;
import autoservice.exception.InvalidDateException;
import autoservice.model.CarServiceMaster;
import autoservice.model.WorkshopPlace;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import autoservice.utils.csv.InputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class CreateOrderAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(CreateOrderAction.class);
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public CreateOrderAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения: создание заказа");

        try {
            System.out.println("\nСоздание нового заказа:");
            Optional<LocalDate> orderDate = admin.getFirstAvailableSlot(LocalDate.now());

            if (orderDate.isEmpty()) {
                logger.warn("Попытка создания заказа: нет свободных дат на ближайшую неделю");
                System.out.println("На ближайшую неделю записи нет.");
                return;
            }

            System.out.print("\nБлижайшая свободная для записи дата: ");
            System.out.println(orderDate.get());

            // Выбор мастера
            List<CarServiceMaster> masters = admin.getCarServiceMasters(CarServiceMastersQuery
                    .builder().localDate(orderDate.get())
                    .isOccupied(false)
                    .sort(SortCarServiceMasters.NAME)
                    .build());

            System.out.println("Доступные мастера:");
            for (int i = 0; i < masters.size(); i++) {
                System.out.println((i + 1) + ". " + masters.get(i).getFullName());
            }
            int masterChoice = InputUtils.readNumberInRange(
                    scanner, "Выберите мастера: ", 1, masters.size());

            // Выбор рабочего места
            List<WorkshopPlace> places = admin.getAvailablePlaces(orderDate.get());
            System.out.println("Доступные рабочие места:");
            for (int i = 0; i < places.size(); i++) {
                System.out.println((i + 1) + ". " + places.get(i).getName());
            }
            int placeChoice = InputUtils.readNumberInRange(
                    scanner, "Выберите рабочее место: ", 1, places.size());

            // Ввод данных заказа
            System.out.print("Описание работ: ");
            String description = scanner.nextLine();

            UUID orderId = admin.createRepairOrder(
                    LocalDate.now(), orderDate.get(), orderDate.get(), description,
                    masters.get(masterChoice - 1), places.get(placeChoice - 1));

            logger.info("Команда 'создание заказа' успешно выполнена. ID заказа: {}, мастер: {}, место: {}",
                    orderId, masters.get(masterChoice - 1).getFullName(), places.get(placeChoice - 1).getName());
            System.out.println("Заказ создан! ID: " + orderId);

        } catch (InvalidDateException e) {
            logger.error("Ошибка выполнения команды 'создание заказа': неверная дата", e);
            System.out.println("Ошибка ввода дат: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка выполнения команды 'создание заказа'", e);
            System.out.println("Ошибка при создании заказа: " + e.getMessage());
        }
    }
}