package com.jvmops.workers.sender.emails.ports;

import com.jvmops.workers.sender.emails.model.EmailMessage;

public interface EmailMessageRepository {
    void emailSent(EmailMessage message);
    void error(EmailMessage emailMessage, Throwable throwable);
}
