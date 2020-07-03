package com.jvmops.workers.sender.emails.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bson.types.ObjectId;

@EqualsAndHashCode(of = "id")
@Value
@Builder
public class PendingEmailMessage {
    ObjectId id;
    int priority;
}
