package com.jvmops.workers.sender.adapters;

import com.jvmops.workers.sender.SmtpClient;
import com.jvmops.workers.sender.model.Email;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Set;

@Lazy
@Component
@Slf4j
@AllArgsConstructor
public class GmailClient implements SmtpClient {
    private JavaMailSender emailSender;

    @Override
    public void send(Email email) {
        try {
            MimeMessageWrapper message = prepareMessage(email.getSubject(), email.getContent());
            sendEmailToReceivers(message, email.getReceivers());
        } catch (MessagingException ex) {
            log.error("Unable to send email: {}", email.getId());
        }
    }

    private MimeMessageWrapper prepareMessage(String subject, String content) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        message.setContent(content, "text/html; charset=utf-8");
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setSubject(subject);

        return new MimeMessageWrapper(message, helper);
    }

    private void sendEmailToReceivers(MimeMessageWrapper messageWrapper, Set<String> emails) throws MessagingException {
        messageWrapper.helper.setTo(emails.toArray(new String[0]));
        emailSender.send(messageWrapper.message);
    }

    private record MimeMessageWrapper(MimeMessage message, MimeMessageHelper helper){}

}
