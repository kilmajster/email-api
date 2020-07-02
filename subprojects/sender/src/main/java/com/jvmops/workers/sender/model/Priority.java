package com.jvmops.workers.sender.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Priority {
    LOW(0),
    MEDIUM(5),
    HIGH(10);

    final int prioryty;
}
