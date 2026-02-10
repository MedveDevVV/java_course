package autoservice.ui;

public abstract class BaseMenuFactory {
    public abstract Menu createMainMenu();
    public abstract Menu createMastersMenu();
    public abstract Menu createOrdersMenu();
    public abstract Menu createPlacesMenu();
}