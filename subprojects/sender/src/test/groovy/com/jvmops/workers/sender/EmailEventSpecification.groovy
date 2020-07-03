package com.jvmops.workers.sender

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(classes= Main, webEnvironment = RANDOM_PORT)
class EmailEventSpecification extends Specification {

    URI emailsEndpoint

    def setup() {
        setupEmailsEndpoint()
        this.emailMessages.deleteAll()
    }

    void setupEmailsEndpoint() {
        this.emailsEndpoint = URI.create("http://localhost:$port/emails")
    }
}
