package autoservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Component
@Scope("singleton")
public final class AppConfig {
    private static final String CONFIG_FILE = "autoservice.properties";

    @Value("${can.add.places:true}")
    private boolean canAddPlaces = true;

    @Value("${can.remove.places:true}")
    private boolean canRemovePlaces = true;

    @Value("${can.delay.orders:true}")
    private boolean canDelayOrders = true;

    @Value("${can.delete.orders:true}")
    private boolean canDeleteOrders = true;

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
