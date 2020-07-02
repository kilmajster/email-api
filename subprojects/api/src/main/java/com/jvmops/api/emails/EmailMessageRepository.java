package com.jvmops.api.emails;

import com.jvmops.api.emails.model.EmailMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface EmailMessageRepository extends MongoRepository<EmailMessage, ObjectId> {
}
