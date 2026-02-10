package autoservice.ui;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private final String name;
    private final List<MenuItem> menuItems = new ArrayList<>();

    public Menu(String name) {
        this.name = name;
    }

    public void addMenuItem(MenuItem item) {
        menuItems.add(item);
    }

    public String getName() { return name; }
    public List<MenuItem> getMenuItems() { return menuItems; }
}