package org.yenln8.ChatApp.service;

import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.dto.SendEmailResponseDto;

@Service
public interface SendOTPChangePasswordService {
    public SendEmailResponseDto sendOTPChangePassword(String recipientEmail);
}