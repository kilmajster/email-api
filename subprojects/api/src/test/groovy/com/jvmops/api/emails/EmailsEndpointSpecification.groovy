package com.jvmops.api.emails

import com.jvmops.api.Main
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(classes= Main, webEnvironment = RANDOM_PORT)
class EmailsEndpointSpecification extends Specification {

    @LocalServerPort
    int port

    URI emailsEndpoint

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    EmailMessageRepository emailMessages

    def setup() {
        setupEmailsEndpoint()
        this.emailMessages.deleteAll()
    }

    void setupEmailsEndpoint() {
        this.emailsEndpoint = URI.create("http://localhost:$port/emails")
    }
}
