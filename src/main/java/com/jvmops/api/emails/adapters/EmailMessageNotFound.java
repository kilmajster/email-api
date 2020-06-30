package com.jvmops.api.emails.adapters;

import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
public class EmailMessageNotFound extends RuntimeException {
    private ObjectId id;

    public EmailMessageNotFound(ObjectId id) {
        super(String.format("Email message not found. Unknown id: %s", id));
        this.id = id;
    }
}
