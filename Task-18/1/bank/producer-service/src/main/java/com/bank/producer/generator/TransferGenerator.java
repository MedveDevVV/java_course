package com.bank.producer.generator;

import com.bank.core.entity.Account;
import com.bank.core.dto.TransferMessage;
import com.bank.producer.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferGenerator {

    private final KafkaProducerService kafkaProducerService;
    private final Random random = new Random();

    public void generateAndSend(Map<Long, Account> accountsMap) {
        List<Long> accountIds = new ArrayList<>(accountsMap.keySet());

        if (accountIds.size() < 2) {
            log.warn("Недостаточно счетов для генерации перевода");
            return;
        }

        Long fromAccountId = getRandomAccountId(accountIds);
        Long toAccountId = getRandomAccountId(accountIds);

        while (fromAccountId.equals(toAccountId)) {
            toAccountId = getRandomAccountId(accountIds);
        }

        UUID transferId = UUID.randomUUID();
        double amount = Math.round((100 + random.nextDouble() * 4900) * 100.0) / 100.0;

        TransferMessage message = new TransferMessage(
                transferId,
                fromAccountId,
                toAccountId,
                amount
        );

        kafkaProducerService.send(message);
        log.info("Отправлено сообщение: transferId={}, from={}, to={}, amount={}",
                transferId, fromAccountId, toAccountId, amount);
    }

    private Long getRandomAccountId(List<Long> accountIds) {
        return accountIds.get(random.nextInt(accountIds.size()));
    }

    public void close() {
        kafkaProducerService.close();
    }
}