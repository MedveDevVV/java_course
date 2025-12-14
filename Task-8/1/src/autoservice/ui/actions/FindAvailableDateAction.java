package autoservice.ui.actions;

import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;

import java.time.LocalDate;
import java.util.Optional;

public class FindAvailableDateAction implements IAction {
    private final AutoServiceAdmin admin;

    public FindAvailableDateAction(AutoServiceAdmin admin) {
        this.admin = admin;
    }

    @Override
    public void execute() {
        System.out.println("\nПоиск ближайшей свободной даты:");
        Optional<LocalDate> availableDate = admin.getFirstAvailableSlot(LocalDate.now());

        if (availableDate.isPresent()) {
            System.out.println("Ближайшая свободная дата: " + availableDate.get());
        } else {
            System.out.println("Нет свободных дат в ближайшую неделю!");
        }
    }
}