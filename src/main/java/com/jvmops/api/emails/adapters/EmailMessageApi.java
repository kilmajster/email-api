package com.jvmops.api.emails.adapters;

import com.jvmops.api.emails.model.EmailMessage;
import com.jvmops.api.emails.model.EmailMessageDto;
import com.jvmops.api.emails.ports.EmailMessageRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.function.Function;

@RestController
@RequestMapping("/emails")
public class EmailMessageApi {

    @Autowired
    private EmailMessageRepository emailMessageRepository;

    @PostMapping
    public EmailMessageDto createEmailMessage(@RequestBody @Valid EmailMessageDto newMessage) {
        return mapToDomainObject()
                .andThen(saveToDabase())
                .andThen(returnId())
                .apply(newMessage);
    }

    private Function<EmailMessageDto, EmailMessage> mapToDomainObject() {
        return dto -> EmailMessage.builder()
                .id(ObjectId.get())
                .sender(dto.getSender())
                .recipients(dto.getRecipients())
                .topic(dto.getTopic())
                .body(dto.getBody())
                .build();
    }

    private Function<EmailMessage, EmailMessage> saveToDabase() {
        return emailMessage -> emailMessageRepository.save(emailMessage);
    }

    private Function<EmailMessage, EmailMessageDto> returnId() {
        return emailMessage -> EmailMessageDto.builder()
                .id(emailMessage.getId())
                .build();
    }
}
