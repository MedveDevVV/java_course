package autoservice.ui;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

public class Navigator {
    private static Navigator instance;
    private final Deque<Menu> menuStack = new ArrayDeque<>();
    private final Scanner scanner = new Scanner(System.in);

    private Navigator() {}

    public static Navigator getInstance() {
        if (instance == null) {
            instance = new Navigator();
        }
        return instance;
    }

    public void setCurrentMenu(Menu menu) {
        menuStack.push(menu);
    }

    public void printMenu() {
        if (menuStack.isEmpty()) {
            System.out.println("Нет доступных меню!");
            return;
        }

        Menu currentMenu = menuStack.peek();
        System.out.println("\n=== " + currentMenu.getName() + " ===");

        // Вывод пунктов меню
        for (int i = 0; i < currentMenu.getMenuItems().size(); i++) {
            System.out.println((i + 1) + ". " + currentMenu.getMenuItems().get(i).title());
        }

        // Вывод кнопки возврата/выхода
        System.out.println("0. " + (menuStack.size() > 1 ? "Назад" : "Выход"));
    }

    public void navigate(int choice) {
        if (menuStack.isEmpty()) {
            System.out.println("Нет доступных меню!");
            return;
        }

        Menu currentMenu = menuStack.peek();

        // Обработка выхода/назад
        if (choice == 0) {
            if (menuStack.size() > 1) {
                menuStack.pop(); // Возврат к предыдущему меню
            } else {
                System.exit(0); // Выход из программы
            }
            return;
        }

        // Обработка выбора пункта меню
        if (choice > 0 && choice <= currentMenu.getMenuItems().size()) {
            MenuItem selectedItem = currentMenu.getMenuItems().get(choice - 1);

            // Выполняем действие пункта меню
            if (selectedItem.action() != null) {
                selectedItem.action().execute();
            }


        } else {
            System.out.println("Неверный выбор! Пожалуйста, введите число от 0 до " + currentMenu.getMenuItems().size());
        }
    }

    public Scanner getScanner() {
        return scanner;
    }
}
