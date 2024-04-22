package com.root.employeeservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq.mail-queue.name}")
    private String queueName;
    @Value("${rabbitmq.mail-routing.key}")
    private String routingKey;
    @Value("${rabbitmq.mail-exchange.name}")
    private String exchangeName;

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Bean
    public Queue queue() {
        LOGGER.info("{} Queue created.", this.queueName);

        return new Queue(this.queueName);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(this.exchangeName);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(this.queue())
                .to(this.exchange())
                .with(this.routingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMessageConverter(this.messageConverter());
        return rabbitTemplate;
    }
}
