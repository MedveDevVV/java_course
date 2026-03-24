package com.bank.producer.service;

import com.bank.core.entity.Account;
import com.bank.producer.generator.TransferGenerator;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProducerService {

    private final AccountService accountService;
    private final TransferGenerator transferGenerator;

    private final Map<Long, Account> accountsMap = new ConcurrentHashMap<>();
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    @PostConstruct
    public void init() {
        log.info("Инициализация ProducerService");
        loadAccounts();
    }

    private void loadAccounts() {
        if (accountService.isAccountsTableEmpty()) {
            log.info("Таблица счетов пуста, генерируем 1000 счетов");
            accountService.generateAndSaveAccounts(1000);
        } else {
            log.info("Таблица счетов не пуста, загружаем существующие счета");
        }

        accountService.findAllAccounts().forEach(account ->
                accountsMap.put(account.getId(), account)
        );
        log.info("Загружено {} счетов", accountsMap.size());
    }

    @Scheduled(fixedDelay = 200)
    public void generateAndSendMessage() {
        if (isRunning.get()) {
            try {
                transferGenerator.generateAndSend(accountsMap);
            } catch (Exception e) {
                log.error("Ошибка при генерации и отправке сообщения", e);
            }
        }
    }

    @PreDestroy
    public void stop() {
        isRunning.set(false);
        transferGenerator.close();
        log.info("ProducerService остановлен");
    }
}