package org.yenln8.ChatApp.services.serviceImpl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.response.SendEmailResponseDto;
import org.yenln8.ChatApp.services.EmailService;
import org.yenln8.ChatApp.services.SendOTPRegistrationService;

import java.util.Random;

@AllArgsConstructor
@Service
public class SendOTPRegistrationServiceImpl implements SendOTPRegistrationService {
    private static final Logger logger = LoggerFactory.getLogger(SendOTPRegistrationServiceImpl.class);
    private EmailService emailService;

    /**
     * Send OTP email for account registration
     *
     * @param recipientEmail Email address to send OTP to
     * @return SendEmailResponseDto with result status
     */
    @Override
    public SendEmailResponseDto sendOTPRegistration(String recipientEmail, String otp) {
        try {
            // Email subject
            String subject = MessageBundle.getMessage("app.email.register.otp.subject");

            // Email content template
            String content = MessageBundle.getMessage("app.email.register.otp.content", otp);
            logger.info(content);
            // Send email using EmailService
            SendEmailResponseDto response = emailService.systemSendTo(recipientEmail, subject, content);
            logger.info("OTP Registration OTP Verification Response: {}", response);

            if (response.getSuccess()) {
                logger.info("Registration OTP sent successfully to: {}", recipientEmail);
            } else {
                logger.error("Failed to send registration OTP to: {}", recipientEmail);
            }
            response.setMessage(MessageBundle.getMessage("app.email.register.success"));

            return response;

        } catch (Exception e) {
            logger.error("Error sending registration OTP to {}: {}", recipientEmail, e.getMessage());
            return SendEmailResponseDto.builder()
                    .success(false)
                    .statusCode(500)
                    .message(MessageBundle.getMessage("app.email.register.fail"))
                    .build();
        }
    }
}
