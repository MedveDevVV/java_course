package autoservice.ui.actions;

import autoservice.model.CarServiceMaster;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import autoservice.utils.csv.InputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Scanner;

public class AddMasterAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(AddMasterAction.class);
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public AddMasterAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения: добавление мастера");
        try {

            System.out.println("\nДобавление мастера:");
            System.out.print("Фамилия: ");
            String surname = scanner.next();
            scanner.nextLine();
            System.out.print("Имя: ");
            String name = scanner.next();
            scanner.nextLine();
            System.out.print("Отчество: ");
            String patronymic = scanner.next();
            scanner.nextLine();
            LocalDate date = InputUtils.readDateInput(scanner, "Дата рождения (гггг-мм-дд): ");
            String fullName = String.join(" ", surname, name, patronymic);
            admin.addMaster(new CarServiceMaster(fullName, date));
            logger.info("Команда 'добавление мастера' успешно выполнена. Мастер: {}", fullName);
            System.out.println("Мастер добавлен!");
        } catch (Exception e){
            logger.error("Ошибка выполнения команды 'добавление мастера'", e);
            System.out.println("Ошибка при добавлении мастера: " + e.getMessage());
        }
    }
}