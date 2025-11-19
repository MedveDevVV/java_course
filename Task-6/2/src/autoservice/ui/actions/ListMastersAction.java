package autoservice.ui.actions;

import autoservice.dto.CarServiceMastersQuery;
import autoservice.enums.SortCarServiceMasters;
import autoservice.model.CarServiceMaster;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.IAction;
import autoservice.utils.csv.InputUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ListMastersAction implements IAction {
    private final AutoServiceAdmin admin;
    private final Scanner scanner;

    public ListMastersAction(AutoServiceAdmin admin, Scanner scanner) {
        this.admin = admin;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\nСписок мастеров - параметры вывода:");
        System.out.println("1. Все мастера");
        System.out.println("2. Мастера занятые в указанную дату");
        System.out.println("3. Мастера свободные в указанную дату");
        int choice = InputUtils.readNumberInRange(scanner,"Выберите вариант: ",1, 3);

        CarServiceMastersQuery.Builder builder = CarServiceMastersQuery.builder();
        List<CarServiceMaster> masters = new ArrayList<>();
        if (choice != 1) {
            switch (choice) {
                case 2:
                    builder.localDate(InputUtils.readDateInput(scanner,
                            "Введите дату для проверки занятости (гггг-мм-дд): "))
                            .isOccupied(true)
                            .sort(SortCarServiceMasters.NAME);
                    break;
                case 3:
                    builder.localDate(InputUtils.readDateInput(scanner,
                                    "Введите дату для проверки занятости (гггг-мм-дд): "))
                            .isOccupied(false)
                            .sort(SortCarServiceMasters.NAME);
                    break;
            }
            masters.addAll(admin.getCarServiceMasters(builder.build()));
        }
        else {
            masters.addAll(admin.getCarServiceMasters());
        }

        System.out.println("Список мастеров:");
        for (int i = 0; i < masters.size(); i++) {
            System.out.println((i + 1) + ". " + masters.get(i).getFullName());
        }
    }
}
