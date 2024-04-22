package com.root.mailservice.consumer;

import com.root.mailservice.entities.EmailEntity;
import com.root.mailservice.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NewMailConsumer {
    @Value("${rabbitmq.queue.name}")
    private String queueName;

    private final MailService mailService;

    private static final Logger LOGGER = LoggerFactory.getLogger(NewMailConsumer.class);

    public NewMailConsumer(MailService mailService) {
        this.mailService = mailService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void newMailConsumer(EmailEntity message) {
        LOGGER.info("Received new message on Queue: {} Message: {}", this.queueName, message.toString());

        this.mailService.sendMailMessage(message);
    }
}
