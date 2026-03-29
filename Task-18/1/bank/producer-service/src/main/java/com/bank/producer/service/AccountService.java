package com.bank.producer.service;

import com.bank.core.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final SessionFactory sessionFactory;
    private final Random random = new Random();

    public boolean isAccountsTableEmpty() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "select count(a) from Account a";
            Long count = (Long) session.createQuery(hql, Long.class).uniqueResult();
            return count == 0;
        }
    }

    public void generateAndSaveAccounts(int count) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            for (int i = 0; i < count; i++) {
                double initialBalance = 1000 + random.nextDouble() * 9000;
                Account account = new Account(Math.round(initialBalance * 100.0) / 100.0);
                session.persist(account);
            }
            transaction.commit();
            log.info("Создано {} счетов", count);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Ошибка при создании счетов", e);
            throw new RuntimeException("Ошибка при создании счетов", e);
        }
    }

    public List<Account> findAllAccounts() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from Account";
            return session.createQuery(hql, Account.class).list();
        }
    }
}
