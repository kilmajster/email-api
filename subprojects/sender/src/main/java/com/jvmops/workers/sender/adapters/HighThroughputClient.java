package com.jvmops.workers.sender.adapters;

import com.jvmops.workers.sender.SmtpClient;
import com.jvmops.workers.sender.model.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Primary
public class HighThroughputClient implements SmtpClient {
    @Override
    public void send(Email email) {
        log.info("It's only a dummy for now!");
    }
}
