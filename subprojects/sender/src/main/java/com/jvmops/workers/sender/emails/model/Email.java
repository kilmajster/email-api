package com.jvmops.workers.sender.emails.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Email {
    ObjectId id;
    String sender;
    Set<String> recipients;
    String topic;
    String body;
    Status status;
    Priority priority;
    Long version;
}
