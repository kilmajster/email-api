package com.jvmops.api.emails.model;

import lombok.*;
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
    Status status;
    Priority priority;
    @Version
    Long version;
}
