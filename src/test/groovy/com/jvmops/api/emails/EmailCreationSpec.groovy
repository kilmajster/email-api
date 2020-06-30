package com.jvmops.api.emails

import com.jvmops.api.Main
import com.jvmops.api.emails.adapters.ErrorMessage
import com.jvmops.api.emails.model.EmailMessage
import com.jvmops.api.emails.model.EmailMessageDto
import com.jvmops.api.emails.model.EmailMessagesDto
import com.jvmops.api.emails.ports.EmailMessageRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static org.springframework.util.CollectionUtils.isEmpty

@SpringBootTest(classes= Main, webEnvironment = RANDOM_PORT)
class EmailCreationSpec extends Specification {
    private static final EmailMessage OLD_EMAIL = oldMessage()
    private static final ObjectId OLD_EMAIL_ID = new ObjectId("5efbc4bb6677c23cec2a1c26")
    private static final EmailMessageDto NEW_VALID_EMAIL = validEmailMessageDto()

    @Autowired
    private EmailMessageRepository emailMessages

    @LocalServerPort
    private int port

    @Autowired
    private TestRestTemplate restTemplate

    private URI emailsEndpoint

    def setup() {
        emailsEndpoint = URI.create("http://localhost:$port/emails")
        emailMessages.deleteAll()
        emailMessages.save(OLD_EMAIL)
    }

    def "GET /emails/{id} - you can retrieve existing email by its id"() {
        when:
        def existingEmails = restTemplate.getForEntity("$emailsEndpoint/$OLD_EMAIL_ID", EmailMessagesDto)
                .getBody()
                .collect()

        then:
        !isEmpty(existingEmails)
    }

    def "GET /emails/{id} - retrieving email by an unknown ID result in 404"() {
        given:
        def randomObjectId = ObjectId.get()

        when:
        def response = restTemplate.getForEntity("$emailsEndpoint/$randomObjectId", ErrorMessage)

        then:
        404 == response.body.statusCode
        and: "With a worthy message"
        response.body.message.contains(randomObjectId.toString())
    }

    def "GET /emails - is paginated and defaults to first page"() {
        when: "no pagination info"
        def emailMessagesDto = restTemplate.getForEntity(emailsEndpoint, EmailMessagesDto)
                .getBody()

        then: "expect first page"
        0 == emailMessagesDto.pageNumber
        and:
        null != emailMessagesDto.maxPages
        null != emailMessagesDto.size
    }

    def "POST /emails - if email was created expect its id and version returned"() {
        when:
        def response = restTemplate.postForEntity(emailsEndpoint, NEW_VALID_EMAIL, EmailMessageDto)

        then:
        null != response.getBody().id
        null != response.getBody().version
    }

    def "POST /emails - created email can be retrieved from db by its id"() {
        when:
        def savedEmailId = restTemplate.postForEntity(emailsEndpoint, NEW_VALID_EMAIL, EmailMessageDto)
            .getBody()
            .getId()
        emailMessages.findById(savedEmailId)

        then:
            noExceptionThrown()
    }

    def "POST /emails - created email can be retrieved through API by its id"() {
        when:
        def savedEmailId = restTemplate.postForEntity(emailsEndpoint, NEW_VALID_EMAIL, EmailMessageDto)
                .getBody()
                .getId()
        def emailDto = restTemplate.getForEntity("$emailsEndpoint/$savedEmailId", EmailMessageDto)
                .getBody();

        then:
        savedEmailId == emailDto.id
    }

    private static EmailMessageDto validEmailMessageDto() {
        EmailMessageDto.builder()
                .sender("jvmops@gmail.com")
                .recipients(Set.of("jvmops@gmail.com"))
                .topic("Important email")
                .body("The message")
                .build()
    }

    private static EmailMessage oldMessage() {
        EmailMessage.builder()
                .id(OLD_EMAIL_ID)
                .sender("jvmops+test@gmail.com")
                .recipients(Set.of("jvmops+test@gmail.com"))
                .topic("Some old email that was created during manual tests")
                .body("Worthless")
                .build()
    }
}
