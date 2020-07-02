package com.jvmops.workers.sender;

import com.jvmops.workers.sender.model.Email;

public interface SmtpClient {
    void send(Email email);
}
