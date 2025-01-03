package com.example.worker;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "";
    public static final String OCR_QUEUE = "ocr_queue";
    public static final String RESULT_QUEUE = "result_queue";

    @Bean
    public Queue ocrQueue() {
        return new Queue(OCR_QUEUE, false);
    }

    @Bean
    public Queue resultQueue() {
        return new Queue(RESULT_QUEUE, false);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("rabbitmq");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setDefaultReceiveQueue(OCR_QUEUE);
        return rabbitTemplate;
    }
}
