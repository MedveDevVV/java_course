package autoservice.database;

import autoservice.exception.DaoException;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class LiquibaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

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

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("!!! ContextRefreshedEvent сработал !!!");
        runLiquibase();
    }
}