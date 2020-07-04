package com.jvmops.api.emails;

import com.jvmops.api.emails.ErrorHandler.EmailMessageNotFound;
import com.jvmops.api.emails.model.*;
import com.jvmops.api.emails.ports.EmailMessageRepository;
import com.jvmops.api.emails.ports.PendingEmailsQueue;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.jvmops.api.emails.EmailMessageApi.Converters.*;
import static com.jvmops.api.emails.model.Status.PENDING;

@RestController
@RequestMapping("/emails")
@AllArgsConstructor
public class EmailMessageApi {
    @Autowired
    private EmailMessageRepository emailMessageRepository;
    @Autowired
    private PendingEmailsQueue pendingEmails;

    @GetMapping
    @RequestMapping("/{emailMessageId}")
    public ResponseEntity<EmailMessageDto> emailMessage(@PathVariable ObjectId emailMessageId) {
        return fetchById()
                .andThen(mapToDto(ProjectionType.SINGLE_EMAIL))
                .andThen(toResponseEntity(HttpStatus.OK))
                 .apply(emailMessageId);
    }

    private Function<ObjectId, EmailMessage> fetchById() {
        return id -> emailMessageRepository.findById(id)
                .orElseThrow(() -> new EmailMessageNotFound(id));
    }

    private <T extends Document> Function<T, ResponseEntity<T>> toResponseEntity(HttpStatus httpStatus) {
        return document -> getBody(httpStatus, document);
    }

    private <T extends Document> ResponseEntity<T> getBody(HttpStatus httpStatus, T document) {
        BodyBuilder bodyBuilder = ResponseEntity.status(httpStatus);

        // TODO: new instanceof pattern matching doesnt work with gradle java plugin? try it
        if (document instanceof VersionedDocument) {
            VersionedDocument versionedDocument = (VersionedDocument) document;
            bodyBuilder.eTag(versionedDocument.eTag());
        }
        return bodyBuilder
                .body(document);
    }


    /**
     * TODO: Implement pagination
     */
    @GetMapping
    public ResponseEntity<EmailMessagesDto> emailMessages() {
        return fetchPage()
                .andThen(mapPageToDto())
                .andThen(toResponseEntity(HttpStatus.OK))
                .apply(Pageable.unpaged());
    }

    private Function<Pageable, Page<EmailMessage>> fetchPage() {
        return pageable -> emailMessageRepository.findAll(pageable);
    }

    @PostMapping
    public EmailMessageDto createEmailMessage(@RequestBody @Valid NewEmailMessageDto newEmailMessage) {
        return saveToDabase()
                .andThen(putOnPendingEmailsQueue())
                .andThen(mapToDto(ProjectionType.ID))
                .apply(newEmailMessage);
    }

    private Function<NewEmailMessageDto, EmailMessage> saveToDabase() {
        return mapToDomain()
                .andThen(emailMessageRepository::save);
    }

    private Function<EmailMessage, EmailMessage> putOnPendingEmailsQueue() {
        return email -> {
            pendingEmails.offer(email);
            return email;
        };
    }

    enum ProjectionType {
        PAGE, SINGLE_EMAIL, ID
    }

    public static class Converters {
        public static Function<Page<EmailMessage>, EmailMessagesDto> mapPageToDto() {
            return Converters::mapPageToDto;
        }

        private static EmailMessagesDto mapPageToDto(Page<EmailMessage> emailMessagePage) {
            var projection = emailMessagePage.getContent().stream()
                    .map(mapToDto(ProjectionType.PAGE))
                    .collect(Collectors.toList());

            var page = new PageImpl<>(
                    projection,
                    emailMessagePage.getPageable(),
                    emailMessagePage.getNumberOfElements());

            return new EmailMessagesDto(page);
        }

        static Function<EmailMessage, EmailMessageDto> mapToDto(ProjectionType projectionType) {
            return (emailMessage) ->
                    switch (projectionType) {
                        case ID -> EmailMessageDto.builder()
                                .id(emailMessage.getId())
                                .build();
                        case SINGLE_EMAIL -> EmailMessageDto.builder()
                                .id(emailMessage.getId())
                                .version(emailMessage.getVersion())
                                .topic(emailMessage.getTopic())
                                .body(emailMessage.getBody())
                                .sender(emailMessage.getSender())
                                .recipients(emailMessage.getRecipients())
                                .priority(emailMessage.getPriority())
                                .status(emailMessage.getStatus())
                                .build();
                        case PAGE -> EmailMessageDto.builder()
                                .id(emailMessage.getId())
                                .topic(emailMessage.getTopic())
                                .sender(emailMessage.getSender())
                                .recipients(emailMessage.getRecipients())
                                .priority(emailMessage.getPriority())
                                .status(emailMessage.getStatus())
                                .build();
                    };
        }

        static Function<NewEmailMessageDto, EmailMessage> mapToDomain() {
            return dto -> EmailMessage.builder()
                    .id(ObjectId.get())
                    .sender(dto.getSender())
                    .recipients(dto.nullSafeRecipients())
                    .topic(dto.getTopic())
                    .body(dto.getBody())
                    .status(PENDING)
                    .priority(lowPriorityIfNull(dto))
                    .build();
        }

        private static Priority lowPriorityIfNull(NewEmailMessageDto dto) {
            return dto.optionalPrioryty()
                    .orElse(Priority.LOW);
        }
    }
}
