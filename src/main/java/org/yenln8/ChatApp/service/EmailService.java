package org.yenln8.ChatApp.service;

import org.yenln8.ChatApp.dto.SendEmailResponseDto;

public interface EmailService {
     public SendEmailResponseDto systemSendTo(String recipientEmail, String subject, String content);
}
