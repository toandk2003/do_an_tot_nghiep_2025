package org.yenln8.ChatApp.services;

import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.dto.response.SendEmailResponseDto;

@Service
public interface SendOTPRegistrationService {
    public SendEmailResponseDto sendOTPRegistration(String recipientEmail);
}