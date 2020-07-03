package com.jvmops.workers.sender.emails.ports;

import com.jvmops.workers.sender.emails.model.EmailMessage;
import com.jvmops.workers.sender.emails.model.Status;

import java.util.function.Function;

public interface SmtpClient {
    void send(EmailMessage email);

    default Function<EmailMessage, EmailMessage> messageSent() {
        return emailMessage -> emailMessage.toBuilder()
                .status(Status.SENT)
                .build();
    }
}
