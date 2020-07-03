package com.jvmops.workers.sender

import com.jvmops.workers.sender.emails.SendEmailCommand
import com.jvmops.workers.sender.emails.adapters.DummyClient
import com.jvmops.workers.sender.emails.adapters.HighThroughputClient
import com.jvmops.workers.sender.emails.model.EmailMessage
import com.jvmops.workers.sender.emails.model.PendingEmailMessage
import com.jvmops.workers.sender.emails.model.Status
import com.jvmops.workers.sender.emails.ports.EmailMessageRepository
import com.jvmops.workers.sender.emails.ports.SmtpClient
import com.jvmops.workers.sender.ports.EmailMessageTestRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static com.jvmops.workers.sender.emails.model.Status.ABORT
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(classes= Main, webEnvironment = NONE)
@ImportAutoConfiguration(exclude = RabbitAutoConfiguration.class)
class EmailSendingSpec extends Specification {
    private static final ObjectId MESSAGE_ID = ObjectId.get()
    private static final EmailMessage PENDING_EMAIL_MESSAGE = emailMessage()
    private static final SmtpClient ALWAYS_SUCCESS = new DummyClient()
    private static final SmtpClient ALWAYS_FAIL = new HighThroughputClient()

    @Autowired
    EmailMessageTestRepository testRepository

    @Autowired
    EmailMessageRepository emailMessageRepository

    SendEmailCommand sendEmailCommand

    def setup() {
        testRepository.save(PENDING_EMAIL_MESSAGE)
    }

    def "After email is dispatched, the message status will be updated to SENT"() {
        given:
        sendEmailCommand = new SendEmailCommand(ALWAYS_SUCCESS, emailMessageRepository)
        def pendingMessage = PendingEmailMessage.builder()
            .id(MESSAGE_ID)
            .build()

        when:
        sendEmailCommand.send(pendingMessage)

        then:
        Status.SENT == emailMessageRepository.findById(MESSAGE_ID)
            .orElse(messageWithStatus(ABORT))
            .getStatus()

    }

    def "If message can't be sent leave it PENDING status be"() {
        given:
        sendEmailCommand = new SendEmailCommand(ALWAYS_FAIL, emailMessageRepository)
        def pendingMessage = PendingEmailMessage.builder()
                .id(MESSAGE_ID)
                .build()

        when:
        sendEmailCommand.send(pendingMessage)

        then:
        thrown(UnsupportedOperationException)

        and:
        Status.PENDING == emailMessageRepository.findById(MESSAGE_ID)
                .orElse(messageWithStatus(ABORT))
                .getStatus()
    }

    static EmailMessage emailMessage() {
        EmailMessage.builder()
                .id(MESSAGE_ID)
                .sender("me+test@jvmops.com")
                .recipients(["you+test@jvmops.com"].toSet())
                .topic("Some irrelevant test email")
                .body("Even move irrelevant stuff is here do not bother to read it.")
                .status(Status.PENDING)
                .priority(0)
                .build()
    }

    static EmailMessage messageWithStatus(Status status) {
        EmailMessage.builder()
                .id(MESSAGE_ID)
                .status(status)
                .build()
    }
}
