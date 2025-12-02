package autoservice.config;

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
    private final Properties properties;

    // Ключи свойств
    public static final String CAN_ADD_PLACES = "can.add.places";
    public static final String CAN_REMOVE_PLACES = "can.remove.places";
    public static final String CAN_DELAY_ORDERS = "can.delay.orders";
    public static final String CAN_DELETE_ORDERS = "can.delete.orders";

    // Значения по-умолчанию
    public static final String DEFAULT_CAN_ADD_PLACES = "true";
    public static final String DEFAULT_CAN_REMOVE_PLACES = "true";
    public static final String DEFAULT_CAN_DELAY_ORDERS = "true";
    public static final String DEFAULT_CAN_DELETE_ORDERS = "true";

    private AppConfig() {
        properties = new Properties();
        loadConfig();
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    private void loadConfig() {
        Path configPath = Paths.get(CONFIG_FILE);

        if (Files.exists(configPath)) {
            try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
                properties.load(input);
            } catch (IOException ex) {
                System.err.println("Ошибка загрузки конфигурации: " + ex.getMessage());
            }
        } else {
            try {
                setDefaults();
                saveConfig();
            } catch (IOException ex) {
                System.err.println("Ошибка создания конфигурации: " + ex.getMessage());
            }
        }
    }

    private void saveConfig() throws IOException{
        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Конфигурация автосервиса");
        }
    }

    private void setDefaults() {
        properties.setProperty(CAN_ADD_PLACES, DEFAULT_CAN_ADD_PLACES);
        properties.setProperty(CAN_REMOVE_PLACES, DEFAULT_CAN_REMOVE_PLACES);
        properties.setProperty(CAN_DELAY_ORDERS, DEFAULT_CAN_DELAY_ORDERS);
        properties.setProperty(CAN_DELETE_ORDERS, DEFAULT_CAN_DELETE_ORDERS);
    }

    public boolean canAddPlaces() {
        return Boolean.parseBoolean(properties.getProperty(CAN_ADD_PLACES, DEFAULT_CAN_ADD_PLACES));
    }

    public boolean canRemovePlaces() {
        return Boolean.parseBoolean(properties.getProperty(CAN_REMOVE_PLACES, DEFAULT_CAN_REMOVE_PLACES));
    }

    public boolean canDelayOrders() {
        return Boolean.parseBoolean(properties.getProperty(CAN_DELAY_ORDERS, DEFAULT_CAN_DELAY_ORDERS));
    }

    public boolean canDeleteOrders() {
        return Boolean.parseBoolean(properties.getProperty(CAN_DELETE_ORDERS, DEFAULT_CAN_DELETE_ORDERS));
    }

}
