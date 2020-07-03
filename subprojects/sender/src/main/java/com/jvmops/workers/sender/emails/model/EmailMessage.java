package com.jvmops.workers.sender.emails.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document("emails")
@EqualsAndHashCode(of = "id")
@Value
@Builder
public class EmailMessage {
    ObjectId id;
    String sender;
    Set<String> recipients;
    String topic;
    String body;
    Status status;
    Priority priority;
    Long version;
}
