package com.jvmops.api.emails.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class EmailMessageDto extends NewEmailMessageDto {
    Status status;
    Long version;
}
