package com.jvmops.api.emails.ports;

import com.jvmops.api.emails.model.EmailMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailMessageRepository extends MongoRepository<EmailMessage, ObjectId> {
}
