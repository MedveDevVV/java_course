package autoservice.ui.actions;

import autoservice.model.CarServiceMaster;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import autoservice.utils.csv.InputUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class RemoveMasterAction implements IAction {
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public RemoveMasterAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\nУдаление мастера:");
        List<CarServiceMaster> masters = admin.getCarServiceMasters();

        if (masters.isEmpty()) {
            System.out.println("Нет доступных мастеров!");
            return;
        }
        System.out.println("Список мастеров:");
        for (int i = 0; i < masters.size(); i++) {
            System.out.println((i + 1) + ". " + masters.get(i).getFullName());
        }

        int choice = InputUtils.readNumberInRange(
                scanner, "Выберите номер мастера для удаления: ", 0, masters.size());
        admin.removeMaster(masters.get(choice - 1));
        System.out.println("Мастер удален!");

    }
}