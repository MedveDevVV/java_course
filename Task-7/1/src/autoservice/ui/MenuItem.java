package autoservice.ui;

public record MenuItem(String title, IAction action) {


    public static MenuItem createNaviItem(String title, Navigator navigator, Menu nextMenu) {
        return new MenuItem(title, () -> navigator.setCurrentMenu(nextMenu));
    }

    public static MenuItem createItem(String title, IAction action) {
        return new MenuItem(title, action);
    }

    // Фабричный метод для пунктов меню с проверкой доступности
    public static MenuItem createConditionalItem(String title, IAction action, boolean isAvailable) {
        return new MenuItem(title, isAvailable ? action : () ->
                System.out.println("Этот пункт меню недоступен"));
    }
}