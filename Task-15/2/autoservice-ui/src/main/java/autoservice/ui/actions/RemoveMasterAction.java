package autoservice.ui.actions;

import autoservice.exception.EntityInUseException;
import autoservice.model.CarServiceMaster;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import autoservice.utils.csv.InputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class RemoveMasterAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(RemoveMasterAction.class);
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public RemoveMasterAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения: удаление мастера");

        try {
            System.out.println("\nУдаление мастера:");
            List<CarServiceMaster> masters = admin.getCarServiceMasters();

            if (masters.isEmpty()) {
                logger.warn("Попытка удаления мастера: нет доступных мастеров");
                System.out.println("Нет доступных мастеров!");
                return;
            }

            System.out.println("Список мастеров:");
            for (int i = 0; i < masters.size(); i++) {
                System.out.println((i + 1) + ". " + masters.get(i).getFullName());
            }

            int choice = InputUtils.readNumberInRange(
                    scanner, "Выберите номер мастера для удаления: ", 0, masters.size());

            CarServiceMaster masterToRemove = masters.get(choice - 1);
            logger.info("Удаление мастера: {} (ID: {})",
                    masterToRemove.getFullName(), masterToRemove.getId());

            admin.removeMaster(masterToRemove);

            logger.info("Команда 'удаление мастера' успешно выполнена. Мастер: {} (ID: {})",
                    masterToRemove.getFullName(), masterToRemove.getId());
            System.out.println("Мастер удален!");
        } catch (EntityInUseException e) {
            logger.error("Ошибка выполнения команды 'удаление мастера': мастер используется в заказах", e);
            System.out.println(e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка выполнения команды 'удаление мастера'", e);
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}