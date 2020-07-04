package com.jvmops.api.emails.model;

public interface VersionedDocument extends Document {
    Long getVersion();

    default String eTag() {
        return Long.toString(getVersion());
    }
}
