package com.jvmops.api.emails.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
public enum Priority {
    LOW(0),
    MEDIUM(5),
    HIGH(10);

    @Getter
    private final int number;

    public static Optional<Priority> with(int number) {
        return Arrays.stream(Priority.values())
                .filter(priority -> priority.getNumber() == number)
                .findFirst();
    }
}
