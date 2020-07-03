package com.jvmops.workers.sender.emails.ports;

import com.jvmops.workers.sender.emails.model.EmailMessage;
import com.jvmops.workers.sender.emails.model.PendingEmailMessage;
import org.bson.types.ObjectId;

import java.util.Optional;

public interface EmailMessageRepository {
    Optional<EmailMessage> findById(ObjectId id);
    void emailSent(EmailMessage message);
    void error(PendingEmailMessage emailMessage, Throwable throwable);
}
