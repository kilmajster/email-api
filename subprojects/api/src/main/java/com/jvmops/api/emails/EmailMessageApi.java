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
                .andThen(Converters.mapToDto(ConvertingStrategy.SINGLE_EMAIL))
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
                .andThen(Converters.mapPageToDto())
                .apply(Pageable.unpaged());
    }

    private Function<Pageable, Page<EmailMessage>> fetchPage() {
        return pageable -> emailMessageRepository.findAll(pageable);
    }

    @PostMapping
    public EmailMessageDto createEmailMessage(@RequestBody @Valid NewEmailMessageDto newEmailMessage) {
        return saveToDabase()
                .andThen(putOnPendingEmailsQueue())
                .andThen(Converters.mapToDto(ConvertingStrategy.ID))
                .apply(newEmailMessage);
    }

    private Function<NewEmailMessageDto, EmailMessage> saveToDabase() {
        return Converters.mapToDomain()
                .andThen(emailMessageRepository::save);
    }

    private Function<EmailMessage, EmailMessage> putOnPendingEmailsQueue() {
        return email -> {
            pendingEmails.offer(email);
            return email;
        };
    }

    enum ConvertingStrategy {
        PAGE, SINGLE_EMAIL, ID
    }

    public static class Converters {
        public static Function<Page<EmailMessage>, EmailMessagesDto> mapPageToDto() {
            return (emailMessagePage) -> {
                var emailMessagesDto = emailMessagePage.getContent().stream()
                        .map(mapToDto(ConvertingStrategy.PAGE))
                        .collect(Collectors.toList());

                return EmailMessagesDto.builder()
                        .pageNumber(emailMessagePage.getNumber())
                        .maxPages(emailMessagePage.getTotalPages())
                        .size(emailMessagePage.getSize())
                        .emailMessages(emailMessagesDto)
                        .build();
            };
        }

        private static Function<EmailMessage, EmailMessageDto> mapToDto(ConvertingStrategy convertingStrategy) {
            return (emailMessage) ->
                    switch (convertingStrategy) {
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

        private static Function<NewEmailMessageDto, EmailMessage> mapToDomain() {
            return dto -> EmailMessage.builder()
                    .id(ObjectId.get())
                    .sender(dto.getSender())
                    .recipients(dto.getRecipients())
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
