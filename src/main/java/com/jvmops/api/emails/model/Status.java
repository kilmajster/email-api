package com.jvmops.api.emails.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Status {
    PENDING(0), SENT(5);

    final int value;
}