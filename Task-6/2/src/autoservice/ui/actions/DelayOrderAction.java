package autoservice.ui.actions;

import autoservice.exception.OrderNotFoundException;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import autoservice.utils.csv.InputUtils;

import java.time.Period;
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
        try {
            UUID orderId = UUID.fromString(orderIdStr);
            int days = InputUtils.readNumberInRange(
                    scanner,"На сколько дней перенести? ", 1, 30);
            admin.delayOrder(orderId, Period.ofDays(days));
            System.out.println("Заказ перенесен на " + days + " дней!");
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный формат ID заказа!");
        } catch (OrderNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}