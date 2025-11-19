package autoservice.ui.actions;

import autoservice.model.WorkshopPlace;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class RemovePlaceAction implements IAction {
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public RemovePlaceAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\nУдаление рабочего места:");
        List<WorkshopPlace> places = admin.getAvailablePlaces(LocalDate.now());

        if (places.isEmpty()) {
            System.out.println("Нет доступных рабочих мест!");
            return;
        }

        System.out.println("Список рабочих мест:");
        for (int i = 0; i < places.size(); i++) {
            System.out.println((i + 1) + ". " + places.get(i).getName());
        }

        System.out.print("Выберите номер места для удаления: ");
        int choice = scanner.nextInt();

        if (choice > 0 && choice <= places.size()) {
            admin.removePlace(places.get(choice - 1));
            System.out.println("Рабочее место удалено!");
        } else {
            System.out.println("Неверный выбор!");
        }
    }
}