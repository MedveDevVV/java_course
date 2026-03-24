package com.bank.producer.service;

import com.bank.core.dto.TransferMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Slf4j
@Service
public class KafkaProducerService {
    private final KafkaProducer<String, TransferMessage> producer;
    private final String topicName;

    public KafkaProducerService(KafkaProducer<String, TransferMessage> producer, String topicName) {
        this.producer = producer;
        this.topicName = topicName;
    }

    public void send(TransferMessage message) {
        try {
            String key = message.transferId().toString();
            ProducerRecord<String, TransferMessage> record = new ProducerRecord<>(topicName, key, message);
            producer.beginTransaction();
            Future<RecordMetadata> future = producer.send(record);
            RecordMetadata metadata = future.get();
            producer.commitTransaction();
            log.debug("Сообщение отправлено в топик {}, партиция {}, offset {}",
                    metadata.topic(), metadata.partition(), metadata.offset());
        } catch (Exception e) {
            try {
                producer.abortTransaction();
            } catch (Exception abortEx) {
                log.error("Ошибка при откате Kafka-транзакции", abortEx);
            }
            log.error("Ошибка при отправке сообщения: {}", message.transferId());
            throw new RuntimeException("Ошибка отправки в Kafka", e);
        }
    }

    public void close() {
        producer.close();
    }
}
