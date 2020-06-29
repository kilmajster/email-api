package com.jvmops.api.emails.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document
@Value
@EqualsAndHashCode(of = "id")
@Builder
public class EmailMessage {
    @Id
    ObjectId id;
    String sender;
    Set<String> recipients;
    String topic;
    String body;
    EmailMessageStatus status;
    @Version
    Long version;

    public enum EmailMessageStatus {
        PENDING, SENT
    }
}
