package autoservice.ui.actions;

import autoservice.model.WorkshopPlace;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;

import java.util.Scanner;

public class AddPlaceAction implements IAction {
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public AddPlaceAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\nДобавление рабочего места:");
        System.out.print("Название места: ");
        String name = scanner.nextLine();

        admin.addWorkshopPlace(new WorkshopPlace(name));
        System.out.println("Рабочее место добавлено!");
    }
}