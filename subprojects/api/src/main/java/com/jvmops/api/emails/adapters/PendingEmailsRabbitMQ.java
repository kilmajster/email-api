package com.jvmops.api.emails.adapters;

import com.jvmops.api.emails.PendingEmailsQueue;
import com.jvmops.api.emails.model.EmailMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import static com.jvmops.api.emails.config.RabbitMQConfig.PENDING_EMAIL_QUEUE;

@Component
@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "host")
@Primary
@Slf4j
@AllArgsConstructor
public class PendingEmailsRabbitMQ implements PendingEmailsQueue {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void offer(EmailMessage email) {
        log.info("Transferring email message on a sending queue. Priority: {}, id: {}", email.getPriority(), email.getId());
        rabbitTemplate.convertAndSend(PENDING_EMAIL_QUEUE, email);
    }
}
