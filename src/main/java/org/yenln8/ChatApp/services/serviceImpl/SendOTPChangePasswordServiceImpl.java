package org.yenln8.ChatApp.services.serviceImpl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.response.SendEmailResponseDto;
import org.yenln8.ChatApp.services.EmailService;
import org.yenln8.ChatApp.services.SendOTPChangePasswordService;

import java.util.Random;

@AllArgsConstructor
@Service
public class SendOTPChangePasswordServiceImpl implements SendOTPChangePasswordService {
    private static final Logger logger = LoggerFactory.getLogger(SendOTPChangePasswordServiceImpl.class);
    private EmailService emailService;

    @Override
    public SendEmailResponseDto sendOTPChangePassword(String recipientEmail, String otp) {
        try {
            // Email subject
            String subject = MessageBundle.getMessage("app.email.change.password.otp.subject");

            // Email content template
            String content = MessageBundle.getMessage("app.email.change.password.otp.content", otp);
            logger.info(content);
            // Send email using EmailService
            SendEmailResponseDto response = emailService.systemSendTo(recipientEmail, subject, content);
            logger.info("OTP Change Password OTP Verification Response: " + response);
            if (response.getSuccess()) {
                logger.info("Change Password  OTP sent successfully to: {}", recipientEmail);
            } else {
                logger.error("Failed to send Change Password OTP to: {}", recipientEmail);
            }
            response.setMessage(MessageBundle.getMessage("app.email.change.password.success"));
            return response;

        } catch (Exception e) {
            logger.error("Error sending Change Password  OTP to {}: {}", recipientEmail, e.getMessage());
            return SendEmailResponseDto.builder()
                    .success(false)
                    .statusCode(500)
                    .message(MessageBundle.getMessage("app.email.change.password.fail"))
                    .build();
        }
    }
}
