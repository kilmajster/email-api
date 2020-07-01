package com.jvmops.api.emails;

import com.jvmops.api.emails.ErrorHandler.EmailMessageNotFound;
import com.jvmops.api.emails.model.EmailMessage;
import com.jvmops.api.emails.model.EmailMessageDto;
import com.jvmops.api.emails.model.EmailMessagesDto;
import com.jvmops.api.emails.model.Priority;
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
public class EmailMessageApi {
    @Autowired
    private EmailMessageRepository emailMessageRepository;

    @GetMapping
    @RequestMapping("/{emailMessageId}")
    public EmailMessageDto emailMessage(@PathVariable ObjectId emailMessageId) {
        return fetchById()
                .andThen(Converters.emailToDto(ConvertingStrategy.SINGLE_EMAIL))
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
    public EmailMessageDto createEmailMessage(@RequestBody @Valid EmailMessageDto newEmailMessage) {
        return saveToDabase()
                .andThen(Converters.emailToDto(ConvertingStrategy.ID))
                .apply(newEmailMessage);
    }

    private Function<EmailMessageDto, EmailMessage> saveToDabase() {
        return Converters.mapToDomainObject()
                .andThen(emailMessageRepository::save);
    }

    enum ConvertingStrategy {
        PAGE, SINGLE_EMAIL, ID
    }

    public static class Converters {
        public static Function<Page<EmailMessage>, EmailMessagesDto> mapPageToDto() {
            return (emailMessagePage) -> {
                var emailMessagesDto = emailMessagePage.getContent().stream()
                        .map(emailToDto(ConvertingStrategy.PAGE))
                        .collect(Collectors.toList());

                return EmailMessagesDto.builder()
                        .pageNumber(emailMessagePage.getNumber())
                        .maxPages(emailMessagePage.getTotalPages())
                        .size(emailMessagePage.getSize())
                        .emailMessages(emailMessagesDto)
                        .build();
            };
        }

        private static Function<EmailMessage, EmailMessageDto> emailToDto(ConvertingStrategy convertingStrategy) {
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

        private static Function<EmailMessageDto, EmailMessage> mapToDomainObject() {
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

        private static Priority lowPriorityIfNull(EmailMessageDto dto) {
            return dto.optionalPrioryty()
                    .orElse(LOW);
        }
    }
}
