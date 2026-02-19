package autoservice.ui;

import autoservice.service.AutoServiceAdmin;
import autoservice.ui.actions.AddMasterAction;
import autoservice.ui.actions.AddPlaceAction;
import autoservice.ui.actions.CancelOrderAction;
import autoservice.ui.actions.CloseOrderAction;
import autoservice.ui.actions.CreateOrderAction;
import autoservice.ui.actions.DelayOrderAction;
import autoservice.ui.actions.ExportMastersAction;
import autoservice.ui.actions.ExportOrdersAction;
import autoservice.ui.actions.ExportPlacesAction;
import autoservice.ui.actions.FindAvailableDateAction;
import autoservice.ui.actions.FindAvailablePlacesAction;
import autoservice.ui.actions.GetOrderByIdAction;
import autoservice.ui.actions.ImportMastersAction;
import autoservice.ui.actions.ImportOrdersAction;
import autoservice.ui.actions.ImportPlacesAction;
import autoservice.ui.actions.ListMastersAction;
import autoservice.ui.actions.RemoveMasterAction;
import autoservice.ui.actions.RemoveOrderAction;
import autoservice.ui.actions.RemovePlaceAction;
import autoservice.ui.actions.ShowOrdersAction;

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

        menu.addMenuItem(MenuItem.createItem("Импорт мастеров из CSV",
                new ImportMastersAction(admin, navigator.getScanner())));

        menu.addMenuItem(MenuItem.createItem("Экспорт мастеров в CSV",
                new ExportMastersAction(admin, navigator.getScanner())));

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

        menu.addMenuItem(MenuItem.createItem("Импорт заказов из CSV",
                new ImportOrdersAction(admin, navigator.getScanner())));

        menu.addMenuItem(MenuItem.createItem("Экспорт заказов в CSV",
                new ExportOrdersAction(admin, navigator.getScanner())));

        menu.addMenuItem(MenuItem.createItem("Удалить заказ",
                new RemoveOrderAction(admin, navigator.getScanner())));

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

        menu.addMenuItem(MenuItem.createItem("Импорт мест из CSV",
                new ImportPlacesAction(admin, navigator.getScanner())));

        menu.addMenuItem(MenuItem.createItem("Экспорт мест в CSV",
                new ExportPlacesAction(admin, navigator.getScanner())));

        return menu;
    }
}
