package com.jvmops.api.emails.model;

import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
public class EmailMessageDto extends NewEmailMessageDto {
    Status status;
    Long version;

    @Builder
    public EmailMessageDto(ObjectId id, Priority priority, @Email(message = "Sender is not a valid email address") @NotEmpty(message = "Missing sender") String sender, Set<@Email(message = "There is an invalid email address among recipients") String> recipients, @NotEmpty(message = "Email topic must be provided") @Size(max = 255, message = "Email topic length exceed 255") String topic, @NotEmpty(message = "Email body must be provided") @Size(max = 102000, message = "Email body length exceed 102000") String body, Status status, Long version) {
        super(id, priority, sender, recipients, topic, body);
        this.status = status;
        this.version = version;
    }
}
