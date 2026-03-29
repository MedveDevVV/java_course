package com.bank.producer;

import com.bank.producer.service.ProducerService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ProducerApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                "com.bank.producer.config",
                "com.bank.producer.service",
                "com.bank.producer.generator"
        );

        ProducerService producerService = context.getBean(ProducerService.class);

        Runtime.getRuntime().addShutdownHook(new Thread(context::close));
    }
}
