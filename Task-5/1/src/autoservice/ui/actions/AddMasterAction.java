package autoservice.ui.actions;

import autoservice.model.CarServiceMaster;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;

import java.time.LocalDate;
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
        String surname = scanner.nextLine();
        System.out.print("Имя: ");
        String name = scanner.nextLine();
        System.out.print("Отчество: ");
        String patronymic = scanner.nextLine();
        System.out.print("Дата рождения (гггг-мм-дд): ");
        String dob = scanner.nextLine();

        admin.addMaster(new CarServiceMaster(surname, name, patronymic, LocalDate.parse(dob)));
        System.out.println("Мастер добавлен!");
    }
}