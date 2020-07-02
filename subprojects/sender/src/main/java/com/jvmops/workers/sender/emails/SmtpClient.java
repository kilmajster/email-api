package com.jvmops.workers.sender.emails;

import com.jvmops.workers.sender.emails.model.Email;

public interface SmtpClient {
    void send(Email email);
}
