package com.jvmops.api.emails;

import com.jvmops.api.emails.model.EmailMessage;

public interface PendingEmailsQueue {
    void offer(EmailMessage emailMessage);
}
