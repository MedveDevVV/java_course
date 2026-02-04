package autoservice.config;

import autoservice.annotation.ConfigProperty;

public final class DatabaseConfig {
    private static final DatabaseConfig INSTANCE = new DatabaseConfig();
    private final String CONFIG_FILE = "database.properties";
    private final String URL_KEY = "db.url";
    private final String USERNAME_KEY = "db.username";
    private final String PASSWORD_KEY = "db.password";
    private final String POOL_SIZE_KEY = "db.pool.size";
    @ConfigProperty(configFileName = CONFIG_FILE, propertyName = URL_KEY)
    private String url;
    @ConfigProperty(propertyName = USERNAME_KEY)
    private String username;
    @ConfigProperty(propertyName = PASSWORD_KEY)
    private String password;
    @ConfigProperty(propertyName = POOL_SIZE_KEY)
    private int poolSize;

    private DatabaseConfig() {
        try {
            AnnotationConfigurator.configure(this);
        } catch (Exception e) {
            System.err.println("Ошибка загрузки конфигурации: " + e.getMessage());
        }
    }

    public static DatabaseConfig getINSTANCE() {
        return INSTANCE;
    }

    public String getUrl() {
        return url;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public Integer getPoolSize() {
        return poolSize;
    }
}
