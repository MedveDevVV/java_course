package com.bank.consumer.service;

import com.bank.core.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final SessionFactory sessionFactory;

    public Account findAccountById(Session session, Long accountId) {
        return session.get(Account.class, accountId);
    }
}