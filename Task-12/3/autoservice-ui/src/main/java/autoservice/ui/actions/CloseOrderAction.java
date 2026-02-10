package autoservice.ui.actions;

import autoservice.exception.OrderNotFoundException;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.UUID;

public class CloseOrderAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(CloseOrderAction.class);
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public CloseOrderAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения: завершение заказа");

        try {
            System.out.println("\nЗавершение заказа:");
            System.out.print("Введите ID заказа: ");
            String orderIdStr = scanner.nextLine();

            UUID orderId = UUID.fromString(orderIdStr);
            logger.info("Завершение заказа с ID: {}", orderId);

            admin.closedOrder(orderId);

            logger.info("Команда 'завершение заказа' успешно выполнена. ID заказа: {}", orderId);
            System.out.println("Заказ завершен!");
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка выполнения команды 'завершение заказа': неверный формат ID", e);
            System.out.println("Неверный формат ID заказа!");
        } catch (OrderNotFoundException e) {
            logger.error("Ошибка выполнения команды 'завершение заказа': заказ не найден", e);
            System.out.println(e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка выполнения команды 'завершение заказа'", e);
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}