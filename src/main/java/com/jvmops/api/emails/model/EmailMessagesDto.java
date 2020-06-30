package com.jvmops.api.emails.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class EmailMessagesDto {
    Integer pageNumber;
    Integer size;
    Integer maxPages;
    List<EmailMessageDto> emailMessages;
}
