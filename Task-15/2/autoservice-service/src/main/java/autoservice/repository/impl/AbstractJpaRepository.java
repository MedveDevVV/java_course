package autoservice.repository.impl;

import autoservice.database.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractJpaRepository {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private JpaUtil jpaUtil;

    protected void executeInTransaction(Consumer<EntityManager> action) {
        EntityManager em = jpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            action.accept(em);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Ошибка при выполнении операции в транзакции", e);
            throw e;
        } finally {
            em.close();
        }
    }

    protected <T> T executeWithResult(Function<EntityManager, T> action) {
        EntityManager em = jpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return action.apply(em);
        } catch (Exception e) {
            logger.error("Ошибка при выполнении запроса", e);
            throw e;
        } finally {
            em.close();
        }
    }

    protected <T> T executeWithResultInTransaction(Function<EntityManager, T> action) {
        EntityManager em = jpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            T result = action.apply(em);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Ошибка при выполнении операции в транзакции", e);
            throw e;
        } finally {
            em.close();
        }
    }
}
