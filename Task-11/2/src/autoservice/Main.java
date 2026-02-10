package autoservice;

import autoservice.di.DIContext;
import autoservice.repository.MasterRepository;
import autoservice.repository.WorkshopPlaceRepository;
import autoservice.repository.impl.*;
import autoservice.service.AutoServiceAdmin;
import autoservice.state.ApplicationState;
import autoservice.state.StateManager;
import autoservice.ui.MenuController;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ApplicationState savedState;
        try {
            savedState = StateManager.loadState();
            System.out.println("Загружено сохраненное состояние: " + savedState);
        } catch (Exception e) {
            System.err.println("Ошибка при запуске приложения: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        DIContext di = createAndConfigureDIContext();
        AutoServiceAdmin admin = di.getInstance(AutoServiceAdmin.class);
        admin.loadState(savedState);
        MenuController menuController = new MenuController(admin);

        addShutdownHook(admin);

        menuController.run();
    }

    private static DIContext createAndConfigureDIContext() {
        DIContext di = new DIContext();

        di.registerImplementation(MasterRepository.class, GarageMasterRepository.class);
        di.registerImplementation(WorkshopPlaceRepository.class, GarageWorkshopPlaceRepository.class);
        di.registerImplementation(autoservice.repository.OrderRepository.class, RepairOrderRepository.class);

        di.registerSingletonType(autoservice.config.AppConfig.class);
        di.registerSingletonType(AutoServiceAdmin.class);

        return di;
    }

    private static void addShutdownHook(AutoServiceAdmin admin) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ApplicationState currentState = admin.getCurrentState();
                StateManager.saveState(currentState);
                System.out.println("Состояние сохранено" + currentState);
            } catch (IOException e) {
                System.err.println("Ошибка при сохранении состояния: " + e.getMessage());
            }
        }));
    }
}