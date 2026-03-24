package com.bank.producer.config;

import com.bank.core.dto.TransferMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;


public class TransferMessageSerializer implements Serializer<TransferMessage> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public byte[] serialize(String s, TransferMessage message) {
        try {
            return objectMapper.writeValueAsBytes(message);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации TransferMessage", e);
        }
    }
}
