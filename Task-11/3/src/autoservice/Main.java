package autoservice;

import autoservice.database.ConnectionManager;
import autoservice.di.DIContext;
import autoservice.repository.MasterRepository;
import autoservice.repository.WorkshopPlaceRepository;
import autoservice.repository.impl.*;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.MenuController;

public class Main {
    public static void main(String[] args) {
        DIContext di = createAndConfigureDIContext();
        AutoServiceAdmin admin = di.getInstance(AutoServiceAdmin.class);
        MenuController menuController = new MenuController(admin);

        menuController.run();
    }

    private static DIContext createAndConfigureDIContext() {
        DIContext di = new DIContext();

        di.registerImplementation(MasterRepository.class, DatabaseMasterRepository.class);
        di.registerImplementation(WorkshopPlaceRepository.class, DatabaseWorkshopPlaceRepository.class);
        di.registerImplementation(autoservice.repository.OrderRepository.class, DatabaseOrderRepository.class);

        di.registerSingletonType(autoservice.config.AppConfig.class);
        di.registerSingletonType(autoservice.config.DatabaseConfig.class);
        di.registerSingletonType(ConnectionManager.class);
        di.registerSingletonType(AutoServiceAdmin.class);

        return di;
    }

}