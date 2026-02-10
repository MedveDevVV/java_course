package autoservice.ui.actions;

import autoservice.dto.RepairOrderQuery;
import autoservice.enums.OrderStatus;
import autoservice.enums.SortRepairOrders;
import autoservice.model.CarServiceMaster;
import autoservice.model.RepairOrder;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import autoservice.utils.csv.InputUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ShowOrdersAction implements IAction {

    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public ShowOrdersAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        RepairOrderQuery.Builder builder = RepairOrderQuery.builder();

        System.out.println("\nФильтрация заказов:");
        System.out.println("1. По статусу");
        System.out.println("2. По мастеру");
        System.out.println("3. За период");
        int choice = InputUtils.readNumberInRange(
                scanner, "Выберите вариант фильтрации: ", 1, 3);

        switch (choice) {
            case 1:
                System.out.println("Доступные статусы:");
                for (OrderStatus status : OrderStatus.values()) {
                    System.out.println(status.ordinal() + ". " + status);
                }
                int statusChoice = InputUtils.readNumberInRange(
                        scanner, "Выберите статус: ", 0, OrderStatus.values().length - 1);
                builder.status(OrderStatus.values()[statusChoice]);
                break;

            case 2:
                List<CarServiceMaster> masters = admin.getCarServiceMasters();
                if (masters.isEmpty()) {
                    System.out.println("Нет доступных мастеров!");
                    return;
                }
                System.out.println("Список мастеров:");
                for (int i = 0; i < masters.size(); i++) {
                    System.out.println((i + 1) + ". " + masters.get(i).getFullName());
                }
                int choiceMaster = InputUtils.readNumberInRange(
                        scanner, "Выберите номер мастера для удаления: ", 1, masters.size());
                builder.carServiceMaster(masters.get(choiceMaster - 1));
                break;

            case 3:
                LocalDate startDate;
                LocalDate endDate;
                while (true) {
                    startDate = InputUtils.readDateInput(scanner, "Введите начальную дату (гггг-мм-дд): ");
                    endDate = InputUtils.readDateInput(scanner, "Введите конечную дату (гггг-мм-дд): ");
                    if (!startDate.isAfter(endDate)) break;
                    System.out.println("Ошибка ввода: Начальная дата не может быть позже конечной.");
                }
                builder.startDate(startDate);
                builder.endDate(endDate);
                break;
        }

        System.out.println("Доступные варианты сортировки:");
        for (SortRepairOrders sort : SortRepairOrders.values()) {
            System.out.println(sort.ordinal() + ". " + sort.name());
        }
        int sortChoice = InputUtils.readNumberInRange(
                scanner, "Выберите сортировку: ", 0, SortRepairOrders.values().length - 1);
        builder.sortOrders(SortRepairOrders.values()[sortChoice]);

        List<RepairOrder> orders = admin.getRepairOrders(builder.build());
        orders.forEach(System.out::println);
    }
}
