package org.yenln8.ChatApp.services.serviceImpl.email.intefaces;

import org.yenln8.ChatApp.dto.response.SendEmailResponseDto;

public interface SendEmailService {
    SendEmailResponseDto call(String recipientEmail, String subject, String content);
}
