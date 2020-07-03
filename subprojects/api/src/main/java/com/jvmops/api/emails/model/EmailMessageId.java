package com.jvmops.api.emails.model;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bson.types.ObjectId;

@Value
@EqualsAndHashCode(of = {"emailMessageId"})
public class EmailMessageId {
    ObjectId emailMessageId;

    private EmailMessageId(ObjectId emailMessageId) {
        this.emailMessageId = emailMessageId;
    }

    static EmailMessageId of(ObjectId emailMessageId) {
        return new EmailMessageId(emailMessageId);
    }
}
