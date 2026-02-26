package autoservice.ui.actions;

import autoservice.exception.OperationNotAllowedException;
import autoservice.exception.OrderNotFoundException;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.UUID;

public class RemoveOrderAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(RemoveOrderAction.class);
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public RemoveOrderAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения: удаление заказа");

        try {
            System.out.println("\nУдаление заказа:");
            System.out.print("Введите ID заказа: ");
            String orderIdStr = scanner.nextLine();

            UUID orderId = UUID.fromString(orderIdStr);
            logger.info("Удаление заказа с ID: {}", orderId);

            admin.removeOrder(orderId);

            logger.info("Команда 'удаление заказа' успешно выполнена. ID заказа: {}", orderId);
            System.out.println("Заказ удален!");
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка выполнения команды 'удаление заказа': неверный формат ID", e);
            System.out.println("Неверный формат ID заказа!");
        } catch (OrderNotFoundException e) {
            logger.error("Ошибка выполнения команды 'удаление заказа': заказ не найден", e);
            System.out.println(e.getMessage());
        } catch (OperationNotAllowedException e) {
            logger.error("Ошибка выполнения команды 'удаление заказа': операция запрещена", e);
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка выполнения команды 'удаление заказа'", e);
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}