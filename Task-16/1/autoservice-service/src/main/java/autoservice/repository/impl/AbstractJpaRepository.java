package autoservice.repository.impl;

import autoservice.database.JpaUtil;
import autoservice.exception.DaoException;
import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractJpaRepository {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private JpaUtil jpaUtil;

    protected void execute(String operationName, Consumer<EntityManager> action, Object... context) {
        try (EntityManager em = jpaUtil.getEntityManagerFactory().createEntityManager()) {
            action.accept(em);
            logging(operationName, context);
        } catch (Exception e) {
            String errorMsg = getErrorMsg(operationName, context);
            logger.error(errorMsg, e);
            throw new DaoException(errorMsg, e);
        }
    }

    protected <T> T executeWithResult(String operationName, Function<EntityManager, T> action, Object... context) {
        try (EntityManager em = jpaUtil.getEntityManagerFactory().createEntityManager()) {
            T t = action.apply(em);
            logging(operationName, context);
            return t;
        } catch (Exception e) {
            String errorMsg = getErrorMsg(operationName, context);
            logger.error(errorMsg, e);
            throw new DaoException(errorMsg, e);
        }
    }

    private static @NonNull String getErrorMsg(String operationName, Object[] context) {
        return String.format("Ошибка операции %s. Контекст: %s", operationName,
                Arrays.toString(context));
    }

    private void logging(String operationName, Object[] context) {
        logger.info("Операция {} успешно выполнена. Контекст: {}", operationName,
                Arrays.toString(context));
    }
}
