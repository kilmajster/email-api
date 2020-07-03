package com.jvmops.workers.sender.emails;

import com.jvmops.workers.sender.emails.model.EmailMessage;
import com.jvmops.workers.sender.emails.model.PendingEmailMessage;
import com.jvmops.workers.sender.emails.ports.EmailMessageRepository;
import com.jvmops.workers.sender.emails.ports.SmtpClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class SendEmailCommand {

    private SmtpClient smtpClient;
    private EmailMessageRepository emailMessageRepository;

    @RabbitListener(queues = "pendingEmails", containerFactory = "pendingEmailsListenerFactory")
    public void send(PendingEmailMessage pendingMessage) {
        geyById().andThen(sendWithErrorHandling(pendingMessage))
                .apply(pendingMessage);
    }
    private  Function<PendingEmailMessage, EmailMessage> geyById() {
        return pendingMessage -> emailMessageRepository.findById(pendingMessage.getId())
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "No such email message: %s", pendingMessage.getId())));
    }

    private Function<EmailMessage, EmailMessage> sendWithErrorHandling(PendingEmailMessage inCaseOfAnError) {
        return emailMessage -> {
            try {
                smtpClient.send(emailMessage);
                return emailMessageRepository.emailSent(emailMessage);
            } catch (Throwable t) {
                // for now it's a dummy, we can handle error this way or in RabbitMQ as DLT pattern
                emailMessageRepository.error(inCaseOfAnError, t);
                throw new UnableToSendEmail(inCaseOfAnError, t);
            }
        };
    }

    public static class UnableToSendEmail extends RuntimeException {
        private static final String MSG = "Unable to send email through SMTP: %s";

        public UnableToSendEmail(PendingEmailMessage pendingMessage, Throwable cause) {
            super(String.format(MSG, pendingMessage.getId()),
                    cause);
        }
    }
}
