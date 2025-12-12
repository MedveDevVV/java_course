package autoservice.ui.actions;

import autoservice.dto.CarServiceMastersQuery;
import autoservice.enums.SortCarServiceMasters;
import autoservice.exception.InvalidDateException;
import autoservice.model.CarServiceMaster;
import autoservice.model.WorkshopPlace;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import autoservice.utils.csv.InputUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class CreateOrderAction implements IAction {
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public CreateOrderAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {

        System.out.println("\nСоздание нового заказа:");
        Optional<LocalDate> orderDate = admin.getFirstAvailableSlot(LocalDate.now());
        if (orderDate.isEmpty()) {
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

        try {
            UUID orderId = admin.createRepairOrder(
                    LocalDate.now(), orderDate.get(), orderDate.get(), description,
                    masters.get(masterChoice - 1), places.get(placeChoice - 1));
            System.out.println("Заказ создан! ID: " + orderId);
        } catch (InvalidDateException e) {
            System.out.println("Ошибка ввода дат: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка при создании заказа: " + e.getMessage());
        }
    }
}