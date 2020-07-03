package com.jvmops.workers.sender.emails.adapters;

import com.jvmops.workers.sender.emails.model.EmailMessage;
import com.jvmops.workers.sender.emails.model.PendingEmailMessage;
import com.jvmops.workers.sender.emails.model.Status;
import com.jvmops.workers.sender.emails.ports.EmailMessageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
public class EmailMessageUpdateRepository implements EmailMessageRepository {
    private MongoTemplate mongoTemplate;

    @Override
    public Optional<EmailMessage> findById(ObjectId id) {
        return Optional.ofNullable(
                mongoTemplate.findById(id, EmailMessage.class));
    }

    @Override
    public void emailSent(EmailMessage emailMessage) {
        mongoTemplate.updateFirst(
                new Query(Criteria
                        .where("id").is(emailMessage.getId())),
                new Update()
                        .set("status", Status.SENT),
                EmailMessage.class
        );
    }

    @Override
    public void error(PendingEmailMessage emailMessage, Throwable throwable) {
        log.warn("Errors are not persisted yet");
    }
}
