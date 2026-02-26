package autoservice;

import autoservice.config.SpringConfig;
import autoservice.service.AutoServiceAdmin;
import autoservice.ui.MenuController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);

        AutoServiceAdmin admin = context.getBean(AutoServiceAdmin.class);
        MenuController menuController = new MenuController(admin);

        menuController.run();
    }
}