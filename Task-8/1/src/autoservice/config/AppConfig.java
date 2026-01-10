package autoservice.config;

import autoservice.annotation.ConfigProperty;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class AppConfig {
    private static final String CONFIG_FILE = "autoservice.properties";
    private static AppConfig instance;

    @ConfigProperty(configFileName = CONFIG_FILE ,propertyName = "can.add.places")
    private boolean canAddPlaces = true;

    @ConfigProperty(propertyName = "can.remove.places")
    private boolean canRemovePlaces = true;

    @ConfigProperty(propertyName = "can.delay.orders")
    private boolean canDelayOrders = true;

    @ConfigProperty(propertyName = "can.delete.orders")
    private boolean canDeleteOrders = true;

    private AppConfig() {
        Path configPath = Paths.get(CONFIG_FILE);

        if (!Files.exists(configPath)) {
            System.out.println("Конфигурационный файл не найден, создается новый");
            saveConfig();
        } else {
            try {
                AnnotationConfigurator.configure(this);
            } catch (Exception e) {
                System.err.println("Ошибка загрузки конфигурации: " + e.getMessage());
            }
        }
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    private void saveConfig() {
        Properties properties = new Properties();
        properties.setProperty("can.add.places", String.valueOf(canAddPlaces));
        properties.setProperty("can.remove.places", String.valueOf(canRemovePlaces));
        properties.setProperty("can.delay.orders", String.valueOf(canDelayOrders));
        properties.setProperty("can.delete.orders", String.valueOf(canDeleteOrders));

        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Конфигурация автосервиса");
        } catch (IOException e) {
            System.err.println("Ошибка создания конфигурационного файла: " + e.getMessage());
        }
    }

    public boolean isCanAddPlaces() {
        return canAddPlaces;
    }

    public boolean isCanRemovePlaces() {
        return canRemovePlaces;
    }

    public boolean isCanDelayOrders() {
        return canDelayOrders;
    }
    public boolean isCanDeleteOrders() {
        return canDeleteOrders;
    }
}
