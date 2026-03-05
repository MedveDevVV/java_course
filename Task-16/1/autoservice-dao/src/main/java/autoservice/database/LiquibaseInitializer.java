package autoservice.database;

import autoservice.exception.DaoException;
import jakarta.annotation.PostConstruct;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class LiquibaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(LiquibaseInitializer.class);
    private final DataSource dataSource;

    public LiquibaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void runLiquibase() {
        try (Connection connection = dataSource.getConnection()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    database);

            liquibase.update();

            System.out.println("Liquibase миграции выполнены успешно");
        } catch (SQLException | LiquibaseException e) {
            throw new DaoException("Ошибка при выполнении Liquibase миграций", e);
        }
    }

    @PostConstruct
    public void init() {
        System.out.println("==============================Initializing Liquibase...===============================");
        runLiquibase();
    }
}