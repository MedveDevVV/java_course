package autoservice.ui.actions;

import autoservice.model.CarServiceMaster;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import autoservice.utils.csv.InputUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class AddMasterAction implements IAction {
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public AddMasterAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
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
        admin.addMaster(new CarServiceMaster(String.join(" ", surname, name, patronymic), date));
        System.out.println("Мастер добавлен!");
    }
}