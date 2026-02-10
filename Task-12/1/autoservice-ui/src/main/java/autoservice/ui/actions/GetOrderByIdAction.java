package autoservice.ui.actions;

import autoservice.exception.OrderNotFoundException;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.UUID;

public class GetOrderByIdAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(GetOrderByIdAction.class);
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public GetOrderByIdAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения: просмотр заказа по ID");

        try {
            System.out.println("\nПросмотр заказа по ID:");
            System.out.print("Введите ID заказа: ");
            String orderIdStr = scanner.nextLine();

            UUID orderId = UUID.fromString(orderIdStr);
            logger.info("Поиск заказа с ID: {}", orderId);

            System.out.println(admin.getOrderById(orderId));

            logger.info("Команда 'просмотр заказа по ID' успешно выполнена. ID заказа: {}", orderId);

        } catch (IllegalArgumentException e) {
            logger.error("Ошибка выполнения команды 'просмотр заказа по ID': неверный формат ID", e);
            System.out.println("Неверный формат ID заказа!");
        } catch (OrderNotFoundException e) {
            logger.error("Ошибка выполнения команды 'просмотр заказа по ID': заказ не найден", e);
            System.out.println(e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка выполнения команды 'просмотр заказа по ID'", e);
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}