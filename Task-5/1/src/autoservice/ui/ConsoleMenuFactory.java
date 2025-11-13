package autoservice.ui;

import autoservice.service.AutoServiceAdmin;
import autoservice.ui.actions.*;

public class ConsoleMenuFactory extends BaseMenuFactory {
    private final AutoServiceAdmin admin;
    private final Navigator navigator;

    public ConsoleMenuFactory(AutoServiceAdmin admin) {
        this.admin = admin;
        this.navigator = Navigator.getInstance();
    }

    @Override
    public Menu createMainMenu() {
        Menu mainMenu = new Menu("Главное меню");

        mainMenu.addMenuItem(MenuItem.createNaviItem("Мастера", navigator, createMastersMenu()));
        mainMenu.addMenuItem(MenuItem.createNaviItem("Заказы", navigator, createOrdersMenu()));
        mainMenu.addMenuItem(MenuItem.createNaviItem("Рабочие места", navigator, createPlacesMenu()));

        return mainMenu;
    }

    @Override
    public Menu createMastersMenu() {
        Menu menu = new Menu("Управление мастерами");

        menu.addMenuItem(MenuItem.createItem("Добавить мастера",
                new AddMasterAction(admin, navigator.getScanner())));

        menu.addMenuItem(MenuItem.createItem("Удалить мастера",
                new RemoveMasterAction(admin, navigator.getScanner())));

        menu.addMenuItem(MenuItem.createItem("Список мастеров",
                new ListMastersAction(admin, navigator.getScanner())));

        return menu;
    }

    @Override
    public Menu createOrdersMenu() {
        Menu menu = new Menu("Управление заказами");

        menu.addMenuItem(MenuItem.createItem("Создать заказ",
                new CreateOrderAction(admin, navigator.getScanner())));

        menu.addMenuItem(MenuItem.createItem("Просмотреть заказ",
                new GetOrderByIdAction(admin, navigator.getScanner())));

        menu.addMenuItem(MenuItem.createItem("Отменить заказ",
                new CancelOrderAction(admin, navigator.getScanner())));

        menu.addMenuItem(MenuItem.createItem("Завершить заказ",
                new CloseOrderAction(admin, navigator.getScanner())));

        menu.addMenuItem(MenuItem.createItem("Перенести заказ",
                new DelayOrderAction(admin, navigator.getScanner())));

        menu.addMenuItem(MenuItem.createItem("Найти свободную дату",
                new FindAvailableDateAction(admin)));

        menu.addMenuItem(MenuItem.createItem("Просмотреть заказы",
                new ShowOrdersAction(admin, navigator.getScanner())));

        return menu;
    }

    @Override
    public Menu createPlacesMenu() {
        Menu menu = new Menu("Управление рабочими местами");

        menu.addMenuItem(MenuItem.createItem("Добавить рабочее место",
                new AddPlaceAction(admin, navigator.getScanner())));

        menu.addMenuItem(MenuItem.createItem("Удалить рабочее место",
                new RemovePlaceAction(admin, navigator.getScanner())));

        menu.addMenuItem(MenuItem.createItem("Найти свободные места",
                new FindAvailablePlacesAction(admin, navigator.getScanner())));

        return menu;
    }
}
