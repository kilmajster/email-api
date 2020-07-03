package com.jvmops.api.emails;

import com.jvmops.api.emails.ErrorHandler.EmailMessageNotFound;
import com.jvmops.api.emails.model.*;
import com.jvmops.api.emails.ports.EmailMessageRepository;
import com.jvmops.api.emails.ports.PendingEmailsQueue;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.jvmops.api.emails.EmailMessageApi.Converters.*;
import static com.jvmops.api.emails.model.Status.PENDING;
import static com.jvmops.api.emails.model.Priority.LOW;

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
    public EmailMessageDto emailMessage(@PathVariable ObjectId emailMessageId) {
        return fetchById()
                .andThen(mapToDto(ProjectionType.SINGLE_EMAIL))
                .apply(emailMessageId);
    }

    private Function<ObjectId, EmailMessage> fetchById() {
        return id -> emailMessageRepository.findById(id)
                .orElseThrow(() -> new EmailMessageNotFound(id));
    }

    /**
     * TODO: Implement pagination
     */
    @GetMapping
    public EmailMessagesDto emailMessages() {
        return fetchPage()
                .andThen(mapPageToDto())
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
            var emailMessagesDto = emailMessagePage.getContent().stream()
                    .map(mapToDto(ProjectionType.PAGE))
                    .collect(Collectors.toList());

            return EmailMessagesDto.builder()
                    .pageNumber(emailMessagePage.getNumber())
                    .maxPages(emailMessagePage.getTotalPages())
                    .size(emailMessagePage.getSize())
                    .emailMessages(emailMessagesDto)
                    .build();
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
                    .orElse(LOW);
        }
    }
}
