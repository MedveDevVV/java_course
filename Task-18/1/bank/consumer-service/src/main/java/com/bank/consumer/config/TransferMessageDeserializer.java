package com.bank.consumer.config;

import com.bank.core.dto.TransferMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public class TransferMessageDeserializer implements Deserializer<TransferMessage> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public TransferMessage deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, TransferMessage.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка десериализации TransferMessage", e);
        }
    }
}
