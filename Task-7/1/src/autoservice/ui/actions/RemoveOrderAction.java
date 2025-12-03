package autoservice.ui.actions;

import autoservice.exception.OperationNotAllowedException;
import autoservice.exception.OrderNotFoundException;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;

import java.util.Scanner;
import java.util.UUID;

public class RemoveOrderAction implements IAction {
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public RemoveOrderAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\nУдаление заказа:");
        System.out.print("Введите ID заказа: ");
        String orderIdStr = scanner.nextLine();

        try {
            UUID orderId = UUID.fromString(orderIdStr);
            admin.removeOrder(orderId);
            System.out.println("Заказ удален!");
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный формат ID заказа!");
        } catch (OrderNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (OperationNotAllowedException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}