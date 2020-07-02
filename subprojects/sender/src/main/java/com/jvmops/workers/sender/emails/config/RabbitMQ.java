package com.jvmops.workers.sender.emails.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;

@EnableConfigurationProperties(RabbitProperties.class)
@Configuration
@Slf4j
public class RabbitMQ {

    public static final String PENDING_EMAIL_QUEUE = "pendingEmails";

    @Autowired
    private RabbitProperties rabbitProperties;

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitProperties.getHost(), rabbitProperties.getPort());
        connectionFactory.setUsername(rabbitProperties.getUsername());
        connectionFactory.setPassword(rabbitProperties.getPassword());

        log.info("Creating connection factory with: {}@{}:{}", rabbitProperties.getUsername(), rabbitProperties.getHost(), rabbitProperties.getPort());

        return connectionFactory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory pendingEmailsListenerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(rabbitConnectionFactory());
        factory.setErrorHandler(errorHandler());
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(4);
        factory.setReceiveTimeout(2000L);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

    public ErrorHandler errorHandler() {
        return new ConditionalRejectingErrorHandler();
    }

    @Bean
    Queue emailMessages() {
        return QueueBuilder.durable(PENDING_EMAIL_QUEUE)
                .maxPriority(5)
                .build();
    }
}
