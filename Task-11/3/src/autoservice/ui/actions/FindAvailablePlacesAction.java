package autoservice.ui.actions;

import autoservice.model.WorkshopPlace;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import autoservice.utils.csv.InputUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class FindAvailablePlacesAction implements IAction {
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public FindAvailablePlacesAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\nПоиск свободных рабочих мест:");
        LocalDate date = InputUtils.readDateInput(scanner, "Введите дату (гггг-мм-дд): ");
        List<WorkshopPlace> places = admin.getAvailablePlaces(date);

        System.out.println("Свободные рабочие места на " + date + ":");
        if (places.isEmpty()) {
            System.out.println("Нет свободных рабочих мест на указанную дату!");
        } else {
            for (int i = 0; i < places.size(); i++) {
                System.out.println((i + 1) + ". " + places.get(i).getName());
            }
        }
    }
}