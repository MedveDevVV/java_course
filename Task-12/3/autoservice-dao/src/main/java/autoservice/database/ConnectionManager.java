package autoservice.database;

import autoservice.config.DatabaseConfig;
import autoservice.exception.DaoException;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class ConnectionManager {
    private static final int DEFAULT_POOL_SIZE = 1;
    private static DatabaseConfig config;
    private static BlockingQueue<Connection> pool;
    private static List<Connection> connections;

    static {
        config = DatabaseConfig.getINSTANCE();
        initConnectionPool();
    }

    private ConnectionManager() {
    }

    private static void initConnectionPool() {
        Integer poolSize = config.getPoolSize();
        int size = poolSize == null ? DEFAULT_POOL_SIZE : poolSize;

        pool = new ArrayBlockingQueue<>(size);
        connections = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            Connection connection = open();
            Connection proxyConnection = (Connection) Proxy.newProxyInstance(
                    ConnectionManager.class.getClassLoader(),
                    new Class[]{Connection.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("close")) {
                            connection.setAutoCommit(true);
                            pool.add(((Connection) proxy));
                            return null;
                        }
                        return method.invoke(connection, args);
                    });
            pool.add(proxyConnection);
            connections.add(connection);
        }
    }

    public static Connection getConnection() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new DaoException("Ошибка получения соединения из пула", e);
        }
    }

    private static Connection open() {
        try {
            return DriverManager.getConnection(
                    config.getUrl(),
                    config.getUsername(),
                    config.getPassword()
            );
        } catch (SQLException e) {
            throw new DaoException("Ошибка подключения к базе данных", e);
        }
    }

    public static void closePool() {
        try {
            for (Connection i : connections) {
                i.close();
            }
        } catch (SQLException e) {
            throw new DaoException("Ошибка закрытия пула соединений", e);
        }
    }
}
