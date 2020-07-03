package com.jvmops.workers.sender.ports

import com.jvmops.workers.sender.emails.model.EmailMessage
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailMessageTestRepository extends MongoRepository<EmailMessage, ObjectId> {

}