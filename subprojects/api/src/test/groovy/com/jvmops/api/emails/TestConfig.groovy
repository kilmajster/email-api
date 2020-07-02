package com.jvmops.api.emails

import com.jvmops.api.emails.model.EmailMessage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestConfig {
    @Bean
    PendingEmailsQueue dummyEmailsQueue() {
        return new DummyEmailsQueue()
    }

    class DummyEmailsQueue implements PendingEmailsQueue {
        @Override
        void offer(EmailMessage emailMessage) {
        }
    }
}
