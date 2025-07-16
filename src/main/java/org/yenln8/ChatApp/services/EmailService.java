package org.yenln8.ChatApp.services;

import org.yenln8.ChatApp.dto.response.SendEmailResponseDto;

public interface EmailService {
     SendEmailResponseDto systemSendTo(String recipientEmail, String subject, String content);
}
