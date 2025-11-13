package autoservice;

import autoservice.repository.RepositoryFactory;
import autoservice.repository.impl.GarageRepositoryFactory;
import autoservice.service.AutoServiceAdmin;
import autoservice.service.AutoServiceAdminFactory;
import autoservice.ui.MenuController;

public class Main {
    public static void main(String[] args) {

        RepositoryFactory repositoryFactory = new GarageRepositoryFactory();
        AutoServiceAdmin admin = AutoServiceAdminFactory.createService(repositoryFactory);

        MenuController menuController = new MenuController(admin);
        menuController.run();
    }
}