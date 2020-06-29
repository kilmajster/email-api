package com.jvmops.api.emails.adapters;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@Builder
public class ErrorMessage {
    OffsetDateTime timestamp;
    int status;
    String error;
    String message;
    String path;
}