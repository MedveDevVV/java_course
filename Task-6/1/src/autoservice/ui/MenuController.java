package autoservice.ui;

import autoservice.service.AutoServiceAdmin;

public class MenuController {
    private final Navigator navigator;

    public MenuController(AutoServiceAdmin admin) {
        this.navigator = Navigator.getInstance();
        BaseMenuFactory menuFactory = new ConsoleMenuFactory(admin);
        this.navigator.setCurrentMenu(menuFactory.createMainMenu());
    }

    public void run() {
        while (true) {
            navigator.printMenu();
            System.out.print("Выберите пункт меню: ");
            try {
                int choice = Integer.parseInt(navigator.getScanner().nextLine());
                navigator.navigate(choice);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число!");
            }
        }
    }
}