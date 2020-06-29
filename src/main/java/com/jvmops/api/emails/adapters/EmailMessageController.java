package com.jvmops.api.emails.adapters;

import com.jvmops.api.emails.model.EmailMessageDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/emails")
public class EmailMessageController {
    @PostMapping
    public EmailMessageDto createEmailMessage(@RequestBody @Valid EmailMessageDto email) {
        return email;
    }
}
