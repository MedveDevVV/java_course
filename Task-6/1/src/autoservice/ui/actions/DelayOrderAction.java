package autoservice.ui.actions;

import autoservice.model.RepairOrder;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;

import java.time.Period;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class DelayOrderAction implements IAction {
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public DelayOrderAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\nПеренос заказа:");
        System.out.print("Введите ID заказа: ");
        String orderIdStr = scanner.nextLine();
        UUID orderId;
        try {
            orderId = UUID.fromString(orderIdStr);
            Optional<RepairOrder> order = admin.getOrderById(orderId);
            if (order.isEmpty()) {
                System.out.println("Заказ с ID " + orderId + " не найден!");
                return;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный формат ID заказа!");
            return;
        }
        System.out.print("На сколько дней перенести? ");
        int days = scanner.nextInt();
        scanner.nextLine();

        admin.delayOrder(orderId, Period.ofDays(days));
        System.out.println("Заказ перенесен на " + days + " дней!");
    }
}