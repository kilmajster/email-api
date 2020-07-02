package com.jvmops.workers.sender.emails.model;

import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

@EqualsAndHashCode(of = "id")
public class PendingEmailMessage {
    ObjectId id;
    Status status;
    Priority priority;
}
