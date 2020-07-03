package com.jvmops.workers.sender.emails;

import com.jvmops.workers.sender.emails.model.EmailMessage;
import com.jvmops.workers.sender.emails.model.PendingEmailMessage;
import com.jvmops.workers.sender.emails.ports.EmailMessageRepository;
import com.jvmops.workers.sender.emails.ports.SmtpClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class SendEmailCommand {

    private SmtpClient smtpClient;
    private EmailMessageRepository emailMessageRepository;

    @RabbitListener(queues = "pendingEmails", containerFactory = "pendingEmailsListenerFactory")
    public void send(PendingEmailMessage pendingMessage) {
        try {
            EmailMessage emailMessage = emailMessageRepository.findById(pendingMessage.getId())
                    .orElseThrow(() -> new IllegalArgumentException(String.format(
                            "No such email message: %s", pendingMessage.getId())));
            smtpClient.send(emailMessage);
            emailMessageRepository.emailSent(emailMessage);
        } catch (Throwable t) {
            emailMessageRepository.error(pendingMessage, t);
            throw t;
        }
    }
}
