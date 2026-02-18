package autoservice.ui;

import autoservice.service.AutoServiceAdmin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuController {
    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);
    private final Navigator navigator;

    public MenuController(AutoServiceAdmin admin) {
        this.navigator = Navigator.getInstance();
        BaseMenuFactory menuFactory = new ConsoleMenuFactory(admin);
        this.navigator.setCurrentMenu(menuFactory.createMainMenu());
    }

    public void run() {
        logger.info("Запуск меню-контроллера");

        while (true) {
            navigator.printMenu();
            System.out.print("Выберите пункт меню: ");
            try {
                int choice = Integer.parseInt(navigator.getScanner().nextLine());
                logger.info("Выбран пункт меню: [{}]", choice);
                navigator.navigate(choice);
                logger.info("Обработка пункта меню [{}] завершена", choice);
            } catch (NumberFormatException e) {
                logger.error("Ошибка ввода: введено не число", e);
                System.out.println("Ошибка: введите число!");
            } catch (Exception e) {
                logger.error("Не известная ошибка при вводе команды", e);
                System.out.println("Произошла ошибка!" + e.getMessage());
            }
        }
    }
}