package com.jvmops.workers.sender.emails;

import com.jvmops.workers.sender.emails.model.Email;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class SendEmailCommand {

    private SmtpClient smtpClient;

    @RabbitListener(queues = "pendingEmails", containerFactory = "pendingEmailsListenerFactory")
    public void send(Email pendingEmailMessage) {
        Email email = new Email();
        smtpClient.send(email);
    }
}
