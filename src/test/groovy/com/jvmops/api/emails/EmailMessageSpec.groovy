package com.jvmops.api.emails

import com.jvmops.api.emails.model.EmailMessageDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
class EmailMessageSpec extends Specification {
    @LocalServerPort
    private int port

    @Autowired
    private TestRestTemplate restTemplate

    private URI emailsEndpoint

    def setup() {
        emailsEndpoint = URI.create("http://localhost:$port/emails")
    }

    def "Email sender can't be null"() {
        given:
        def email = EmailMessageDto.builder()
                .recipients(Set.of("jvmops@gmail.com"))
                .topic("Important email")
                .body("The message")
                .build()

        when:
        def response = restTemplate.postForEntity(emailsEndpoint, email, EmailMessageDto)

        then:
        response.statusCode.is4xxClientError()
    }

    def "Email sender can't be an empty string"() {
        given:
        def email = EmailMessageDto.builder()
                .sender("")
                .recipients(Set.of("jvmops@gmail.com"))
                .topic("Important email")
                .body("The message")
                .build()

        when:
        def response = restTemplate.postForEntity(emailsEndpoint, email, EmailMessageDto)

        then:
        response.statusCode.is4xxClientError()
    }

    def "Email sender needs to be a valid email address"() {
        given:
        def email = EmailMessageDto.builder()
                .sender("jvmops@gmail..com")
                .recipients(Set.of("jvmops@gmail.com"))
                .topic("Important email")
                .body("The message")
                .build()

        when:
        def response = restTemplate.postForEntity(emailsEndpoint, email, EmailMessageDto)

        then:
        response.statusCode.is4xxClientError()
    }

    def "Email recipients can't be null"() {
        given:
        def email = EmailMessageDto.builder()
                .sender("jvmops@gmail.com")
                .topic("Important email")
                .body("The message")
                .build()

        when:
        def response = restTemplate.postForEntity(emailsEndpoint, email, EmailMessageDto)

        then:
        response.statusCode.is4xxClientError()
    }

    def "Email recipients can't be an empty set"() {
        given:
        def email = EmailMessageDto.builder()
                .sender("jvmops@gmail.com")
                .recipients(Set.of())
                .topic("Important email")
                .body("The message")
                .build()

        when:
        def response = restTemplate.postForEntity(emailsEndpoint, email, EmailMessageDto)

        then:
        response.statusCode.is4xxClientError()
    }

    def "Email recipients must contain valid email addresses"() {
        given:
        def email = EmailMessageDto.builder()
                .sender("jvmops@gmail.com")
                .recipients(Set.of("jvmops@gmail.com", "jvmops@gm..com"))
                .topic("Important email")
                .body("The message")
                .build()

        when:
        def response = restTemplate.postForEntity(emailsEndpoint, email, EmailMessageDto)

        then:
        response.statusCode.is4xxClientError()
    }

    def "Email topic must not be empty"() {
        given:
        def email = EmailMessageDto.builder()
                .sender("jvmops@gmail.com")
                .recipients(Set.of("jvmops@gmail.com", "jvmops@gm..com"))
                .topic("")
                .body("The message")
                .build()

        when:
        def response = restTemplate.postForEntity(emailsEndpoint, email, EmailMessageDto)

        then:
        response.statusCode.is4xxClientError()
    }

    def "Email body can't be empty "() {
        given:
        def email = EmailMessageDto.builder()
                .sender("jvmops@gmail.com")
                .recipients(Set.of("jvmops@gmail.com", "jvmops@gm..com"))
                .topic("")
                .body("The message")
                .build()

        when:
        def response = restTemplate.postForEntity(emailsEndpoint, email, EmailMessageDto)

        then:
        response.statusCode.is4xxClientError()
    }
}