package autoservice.ui.actions;

import autoservice.model.WorkshopPlace;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import autoservice.utils.csv.InputUtils;

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
        int choice = InputUtils.readNumberInRange(
                scanner, "Выберите номер места для удаления: ", 0, places.size());

        admin.removePlace(places.get(choice - 1));
        System.out.println("Рабочее место удалено!");
    }
}