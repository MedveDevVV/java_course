package com.bank.consumer;

import com.bank.consumer.service.ConsumerService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ConsumerApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                "com.bank.consumer.config",
                "com.bank.consumer.service"
        );

        ConsumerService consumerService = context.getBean(ConsumerService.class);

        Runtime.getRuntime().addShutdownHook(new Thread(context::close));
    }
}