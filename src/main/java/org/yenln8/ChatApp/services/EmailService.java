package org.yenln8.ChatApp.services;

import org.yenln8.ChatApp.dto.response.SendEmailResponseDto;

public interface EmailService {
     public SendEmailResponseDto systemSendTo(String recipientEmail, String subject, String content);

     public void sendOTPChangePassword(String recipientEmail, String otp);

     public void sendOTPRegistration(String recipientEmail, String otp);

     public void sendOTPResetPassword(String recipientEmail, String otp);

}
