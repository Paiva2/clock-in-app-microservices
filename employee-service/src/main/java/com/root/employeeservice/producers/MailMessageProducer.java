package com.root.employeeservice.producers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailMessageProducer {
    @Value("${rabbitmq.mail-routing.key}")
    private String routingKey;
    @Value("${rabbitmq.mail-exchange.name}")
    private String exchangeName;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailMessageProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public NewMailProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void produceMailMessage(String message) {
        this.rabbitTemplate.convertAndSend(this.exchangeName, this.routingKey, message);

        LOGGER.info("New message sent from Mail Message Producer!");
    }
}
