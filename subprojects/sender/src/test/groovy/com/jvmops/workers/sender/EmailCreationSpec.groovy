package com.jvmops.api.emails

import com.jvmops.workers.Main
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Ignore
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(classes= Main, webEnvironment = NONE)
class EmailCreationSpec extends Specification {

    def setup() {
    }

    @Ignore
    def "POST /emails - if email was created expect its id to be returned"() {
        when:
        def response = restTemplate.postForEntity(emailsEndpoint, validEmailMessageDto(), EmailMessageDto)

        then:
        null != response.getBody().id
    }

    @Ignore
    def "POST /emails - created email can be retrieved through API by its id"() {
        when:
        def savedEmailId = restTemplate.postForEntity(emailsEndpoint, validEmailMessageDto(), EmailMessageDto)
                .getBody()
                .getId()
        def emailDto = restTemplate.getForEntity("$emailsEndpoint/$savedEmailId", EmailMessageDto)
                .getBody();

        then:
        savedEmailId == emailDto.id
    }
}
