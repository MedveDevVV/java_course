package autoservice.ui.actions;

import autoservice.model.RepairOrder;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;

import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class GetOrderByIdAction implements IAction {
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public GetOrderByIdAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\nПросмотр заказа по ID:");
        System.out.print("Введите ID заказа: ");
        String orderIdStr = scanner.nextLine();

        try {
            UUID orderId = UUID.fromString(orderIdStr);
            Optional<RepairOrder> order = admin.getOrderById(orderId);

            if (order.isPresent()) {
                System.out.println(order.get());
            } else {
                System.out.println("Заказ с ID " + orderId + " не найден!");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный формат ID заказа!");
        }
    }
}