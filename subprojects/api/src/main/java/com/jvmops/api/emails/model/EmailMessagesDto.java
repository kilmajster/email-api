package com.jvmops.api.emails.model;

import com.google.common.hash.Hashing;
import lombok.*;
import lombok.experimental.Delegate;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.beans.Transient;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Value
public class EmailMessagesDto implements Pageable, VersionedDocument {

    @Delegate
    Pageable pageable;

    List<EmailMessageDto> content;

    public EmailMessagesDto(Page<EmailMessageDto> page) {
        this.pageable = page.getPageable();
        this.content = page.getContent();
    }

    @Override
    public Long getVersion() {
        return -1L;
    }

    @Override
    @Transient
    public ObjectId getId() {
        return ObjectId.get();
    }

    @Override
    @Transient
    public String eTag() {
        return hash(combinedIds());
    }

    private String combinedIds() {
        return getContent().stream()
                .map(dto -> dto.getId())
                .map(ObjectId::toString)
                .collect(Collectors.joining());
    }

    private String hash(String source) {
        return Hashing.sha256()
                .hashString(source, StandardCharsets.UTF_8)
                .toString();
    }
}
