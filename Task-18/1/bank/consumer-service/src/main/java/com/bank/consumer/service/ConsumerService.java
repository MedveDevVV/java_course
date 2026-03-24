package com.bank.consumer.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerService {

    private final KafkaConsumerService kafkaConsumerService;

    @PostConstruct
    public void init() {
        log.info("Запуск ConsumerService");
        kafkaConsumerService.start();
    }

    @PreDestroy
    public void stop() {
        log.info("Остановка ConsumerService");
        kafkaConsumerService.stop();
    }
}