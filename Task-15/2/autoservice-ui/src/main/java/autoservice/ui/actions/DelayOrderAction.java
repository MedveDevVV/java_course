package autoservice.ui.actions;

import autoservice.exception.OperationNotAllowedException;
import autoservice.exception.OrderNotFoundException;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import autoservice.utils.csv.InputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Period;
import java.util.Scanner;
import java.util.UUID;

public class DelayOrderAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(DelayOrderAction.class);
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public DelayOrderAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения: перенос заказа");

        try {
            System.out.println("\nПеренос заказа:");
            System.out.print("Введите ID заказа: ");
            String orderIdStr = scanner.nextLine();

            UUID orderId = UUID.fromString(orderIdStr);
            int days = InputUtils.readNumberInRange(
                    scanner, "На сколько дней перенести? ", 1, 30);

            logger.info("Перенос заказа ID: {} на {} дней", orderId, days);

            admin.delayOrder(orderId, Period.ofDays(days));

            logger.info("Команда 'перенос заказа' успешно выполнена. ID заказа: {}, перенос на {} дней",
                    orderId, days);
            System.out.println("Заказ перенесен на " + days + " дней!");
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка выполнения команды 'перенос заказа': неверный формат ID", e);
            System.out.println("Неверный формат ID заказа!");
        } catch (OrderNotFoundException e) {
            logger.error("Ошибка выполнения команды 'перенос заказа': заказ не найден", e);
            System.out.println(e.getMessage());
        } catch (OperationNotAllowedException e) {
            logger.error("Ошибка выполнения команды 'перенос заказа': операция запрещена", e);
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка выполнения команды 'перенос заказа'", e);
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}