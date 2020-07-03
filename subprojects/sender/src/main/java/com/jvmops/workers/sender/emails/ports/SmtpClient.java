package com.jvmops.workers.sender.emails.ports;

import com.jvmops.workers.sender.emails.model.EmailMessage;

public interface SmtpClient {
    void send(EmailMessage email);
}
