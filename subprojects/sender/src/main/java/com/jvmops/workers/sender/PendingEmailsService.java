package com.jvmops.workers.sender;

import com.jvmops.workers.sender.model.Email;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class PendingEmailsService {

    private SmtpClient smtpClient;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "pendingEmails", durable = "true"),
            exchange = @Exchange(value = "auto.exch"),
            key = "pendingEmails"))
    public void send(String pendingEmailMessage) {
        Email email = new Email();
        smtpClient.send(email);
        log.info("consumed!");
    }
}
