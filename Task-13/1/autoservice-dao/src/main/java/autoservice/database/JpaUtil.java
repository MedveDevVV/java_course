package autoservice.database;

import autoservice.config.DatabaseConfig;
import autoservice.exception.DaoException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class JpaUtil {
    private static final Logger logger = LoggerFactory.getLogger(JpaUtil.class);
    private static final String PERSISTENCE_UNIT_NAME = "autoservice-pu";
    private static volatile EntityManagerFactory emf;

    private JpaUtil() {
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            synchronized (JpaUtil.class) {
                if (emf == null) {
                    try {
                        DatabaseConfig config = DatabaseConfig.getINSTANCE();
                        Thread.currentThread().setContextClassLoader(JpaUtil.class.getClassLoader());

                        Map<String, Object> properties = new HashMap<>();
                        properties.put("jakarta.persistence.jdbc.url", config.getUrl());
                        properties.put("jakarta.persistence.jdbc.user", config.getUsername());
                        properties.put("jakarta.persistence.jdbc.password", config.getPassword());
                        properties.put("hibernate.connection.pool_size", config.getPoolSize());
                        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
                        logger.info("Hibernate EntityManagerFactory успешно инициализирована");
                    } catch (Exception e) {
                        logger.error("Ошибка инициализации Hibernate EntityManagerFactory", e);
                        throw new DaoException("Не удалось инициализировать Hibernate", e);
                    }
                }
            }
        }
        return emf;
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            logger.info("EntityManagerFactory закрыта");
        }
    }
}
