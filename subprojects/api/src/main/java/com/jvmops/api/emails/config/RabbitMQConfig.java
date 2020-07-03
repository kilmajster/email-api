package com.jvmops.api.emails.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "host")
@Slf4j
public class RabbitMQConfig {
    public static final String PENDING_EMAIL_QUEUE = "pendingEmails";

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        ObjectMapper mapper = new Jackson2ObjectMapperBuilder()
                .serializerByType(ObjectId.class, new ToStringSerializer())
                .build();
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(mapper);
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }

    @Bean
    Queue emailMessages() {
        return QueueBuilder.durable(PENDING_EMAIL_QUEUE)
                .maxPriority(10)
                .build();
    }
}
