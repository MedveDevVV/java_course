package com.bank.consumer.service;

import com.bank.core.dto.TransferMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class KafkaConsumerService {

    private final Properties kafkaConsumerProperties;
    private final String topicName;
    private final TransferProcessor transferProcessor;

    private KafkaConsumer<String, TransferMessage> consumer;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread consumerThread;

    public KafkaConsumerService(
            Properties kafkaConsumerProperties,
            String topicName,
            TransferProcessor transferProcessor
    ) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
        this.topicName = topicName;
        this.transferProcessor = transferProcessor;
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            consumer = new KafkaConsumer<>(kafkaConsumerProperties);
            consumer.subscribe(List.of(topicName));

            consumerThread = new Thread(() -> {
                while (running.get()) {
                    try {
                        ConsumerRecords<String, TransferMessage> records = consumer.poll(Duration.ofMillis(1000));
                        if (records.isEmpty()) {
                            continue;
                        }

                        log.info("Получено {} сообщений", records.count());

                        for (ConsumerRecord<String, TransferMessage> record : records) {
                            TransferMessage message = record.value();
                            transferProcessor.process(message);
                        }

                        consumer.commitSync();
                        log.info("Смещения закоммичены");
                    } catch (Exception e) {
                        log.error("Ошибка при обработке сообщений", e);
                    }
                }
            });
            consumerThread.start();
            log.info("KafkaConsumerService запущен");
        }
    }

    public void stop() {
        if (running.compareAndSet(true, false)) {
            if (consumer != null) {
                consumer.wakeup();
            }
            if (consumerThread != null) {
                try {
                    consumerThread.join(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            if (consumer != null) {
                consumer.close();
            }
            log.info("KafkaConsumerService остановлен");
        }
    }
}