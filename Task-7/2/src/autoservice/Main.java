package autoservice;

import autoservice.repository.RepositoryFactory;
import autoservice.repository.impl.GarageRepositoryFactory;
import autoservice.service.AutoServiceAdmin;
import autoservice.service.AutoServiceAdminFactory;
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

        RepositoryFactory repositoryFactory = new GarageRepositoryFactory();
        AutoServiceAdmin admin = AutoServiceAdminFactory.createService(repositoryFactory);
        admin.loadState(savedState);
        MenuController menuController = new MenuController(admin);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ApplicationState currentState = admin.getCurrentState();
                StateManager.saveState(currentState);
                System.out.println("Состояние сохранено" + currentState);
            } catch (IOException e) {
                System.err.println("Ошибка при сохранении состояния: " + e.getMessage());
            }
        }));

        menuController.run();
    }
}