package autoservice.ui.actions;

import autoservice.dto.OrderQuery;
import autoservice.enums.OrderStatus;
import autoservice.enums.SortOrders;
import autoservice.model.CarServiceMaster;
import autoservice.model.Order;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;

import java.time.LocalDate;
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
        OrderQuery.Builder builder = OrderQuery.builder();

        System.out.println("\nФильтрация заказов:");
        System.out.println("1. По статусу");
        System.out.println("2. По мастеру");
        System.out.println("3. За период");
        System.out.print("Выберите вариант фильтрации: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.println("Доступные статусы:");
                for (OrderStatus status : OrderStatus.values()) {
                    System.out.println(status.ordinal() + ". " + status);
                }
                System.out.print("Выберите статус: ");
                int statusChoice = scanner.nextInt();
                scanner.nextLine();

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

                System.out.print("Выберите номер мастера для удаления: ");
                choice = scanner.nextInt();
                scanner.nextLine();

                if (choice > 0 && choice <= masters.size()) {
                    builder.carServiceMaster(masters.get(choice - 1));
                } else {
                    System.out.println("Неверный выбор!");
                }
                break;

            case 3:
                System.out.print("Введите начальную дату (гггг-мм-дд): ");
                String startDate = scanner.nextLine();
                System.out.print("Введите конечную дату (гггг-мм-дд): ");
                String endDate = scanner.nextLine();
                builder.startDate(LocalDate.parse(startDate));
                builder.endDate(LocalDate.parse(endDate));
                break;
        }

        System.out.println("Доступные варианты сортировки:");
        for (SortOrders sort : SortOrders.values()) {
            System.out.println(sort.ordinal() + ". " + sort.name());
        }
        System.out.print("Выберите сортировку: ");
        int sortChoice = scanner.nextInt();
        scanner.nextLine();

        builder.sortOrders(SortOrders.values()[sortChoice]);


        List<Order> orders = admin.getOrders(builder.build());
        orders.forEach(System.out::println);
    }
}
