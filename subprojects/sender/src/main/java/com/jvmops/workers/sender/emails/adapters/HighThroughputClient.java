package com.jvmops.workers.sender.emails.adapters;

import com.jvmops.workers.sender.emails.ports.SmtpClient;
import com.jvmops.workers.sender.emails.model.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
@Primary
@Profile("prod")
public class HighThroughputClient implements SmtpClient {
    @Override
    public void send(EmailMessage email) {
        throw new UnsupportedOperationException("High throughput SMTP client is not implemented yet!");
    }

    @PostConstruct
    @SuppressWarnings("PMD")
    private void log() {
        log.warn("High throughput SMTP client registered!");
    }
}
