package org.yenln8.ChatApp.service;

import org.yenln8.ChatApp.dto.response.SendEmailResponseDto;

public interface EmailService {
     SendEmailResponseDto systemSendTo(String recipientEmail, String subject, String content);
}
