package com.jvmops.workers.sender.emails.adapters;

import com.jvmops.workers.sender.emails.model.EmailMessage;
import com.jvmops.workers.sender.emails.model.Status;
import com.jvmops.workers.sender.emails.ports.EmailMessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class EmailMessageUpdateRepository implements EmailMessageRepository {
    private MongoTemplate mongoTemplate;

    @Override
    public void emailSent(EmailMessage emailMessage) {
        mongoTemplate.updateFirst(
                new Query(Criteria.where("id").is(emailMessage.getId())),
                new Update().set("status", Status.SENT),
                EmailMessage.class
        );
    }

    @Override
    public void error(EmailMessage emailMessage, Throwable throwable) {

    }
}
