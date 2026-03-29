package com.bank.consumer.service;

import com.bank.core.dto.TransferMessage;
import com.bank.core.entity.Account;
import com.bank.core.entity.Transfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferProcessor {

    private final SessionFactory sessionFactory;
    private final AccountService accountService;

    public void process(TransferMessage message) {
        log.info("Начало обработки сообщения: transferId={}", message.transferId());

        try (Session session = sessionFactory.openSession()) {
            Account fromAccount = accountService.findAccountById(session, message.fromAccountId());
            Account toAccount = accountService.findAccountById(session, message.toAccountId());

            if (fromAccount == null || toAccount == null) {
                log.error("Ошибка валидации: счет не найден. fromId={}, toId={}",
                        message.fromAccountId(), message.toAccountId());
                return;
            }

            if (fromAccount.getBalance() < message.amount()) {
                log.error("Ошибка валидации: недостаточно средств. accountId={}, balance={}, amount={}",
                        message.fromAccountId(), fromAccount.getBalance(), message.amount());
                return;
            }

            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                fromAccount.setBalance(fromAccount.getBalance() - message.amount());
                toAccount.setBalance(toAccount.getBalance() + message.amount());
                session.merge(fromAccount);
                session.merge(toAccount);

                Transfer transfer = new Transfer(
                        message.transferId(),
                        message.fromAccountId(),
                        message.toAccountId(),
                        message.amount(),
                        "готово"
                );
                session.persist(transfer);

                tx.commit();
                log.info("Успешная обработка сообщения: transferId={}", message.transferId());
            } catch (Exception e) {
                safeRollback(tx, message.transferId());
                log.error("Ошибка транзакции при обработке сообщения: transferId={}", message.transferId(), e);
                saveTransferWithError(message);
            }
        } catch (Exception e) {
            log.error("Критическая ошибка обработки сообщения: transferId={}", message.transferId(), e);
            saveTransferWithError(message);
        }
    }

    private void safeRollback(Transaction tx, java.util.UUID transferId) {
        if (tx == null) {
            return;
        }
        try {
            tx.rollback();
        } catch (Exception rollbackException) {
            log.error("Ошибка rollback транзакции: transferId={}", transferId, rollbackException);
        }
    }

    private void saveTransferWithError(TransferMessage message) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Transfer transfer = new Transfer(
                    message.transferId(),
                    message.fromAccountId(),
                    message.toAccountId(),
                    message.amount(),
                    "завершилось с ошибкой"
            );
            session.persist(transfer);
            tx.commit();
        } catch (Exception e) {
            log.error("Не удалось сохранить перевод с ошибкой: transferId={}", message.transferId(), e);
        }
    }
}