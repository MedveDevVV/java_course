package autoservice.repository.impl;

import autoservice.exception.DaoException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor
public abstract class AbstractJpaRepository {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final SessionFactory sessionFactory;

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    protected void execute(String operationName, Consumer<Session> action, Object... context) {
        try {
            Session session = getCurrentSession();
            action.accept(session);
            logging(operationName, context);
        } catch (Exception e) {
            String errorMsg = getErrorMsg(operationName, context);
            logger.error(errorMsg, e);
            throw new DaoException(errorMsg, e);
        }
    }

    protected <T> T executeWithResult(String operationName, Function<Session, T> action, Object... context) {
        try {
            Session session = getCurrentSession();
            T t = action.apply(session);
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