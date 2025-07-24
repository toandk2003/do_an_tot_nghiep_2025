package org.yenln8.ChatApp.services.serviceImpl.email.implement;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.response.SendEmailResponseDto;
import org.yenln8.ChatApp.services.serviceImpl.email.intefaces.SendOTPRegistrationService;

@AllArgsConstructor
@Service
public class SendOTPRegistrationServiceImpl implements SendOTPRegistrationService {
    private static final Logger logger = LoggerFactory.getLogger(SendOTPRegistrationServiceImpl.class);
    private SendEmailServiceImpl sendEmailService;

    /**
     * Send OTP email for account registration
     *
     * @param recipientEmail Email address to send OTP to
     */
    @Override
    public void call(String recipientEmail, String otp) {
        try {
            // Email subject
            String subject = MessageBundle.getMessage("app.email.register.otp.subject");

            // Email content template
            String content = MessageBundle.getMessage("app.email.register.otp.content", otp);
            logger.info(content);
            // Send email using EmailService
            SendEmailResponseDto response = sendEmailService.call(recipientEmail, subject, content);
            logger.info("OTP Registration OTP Verification Response: {}", response);

            if (response.getSuccess()) {
                logger.info("Registration OTP sent successfully to: {}", recipientEmail);
            } else {
                logger.error("Failed to send registration OTP to: {}", recipientEmail);
            }
            response.setMessage(MessageBundle.getMessage("app.email.register.success"));

        } catch (Exception e) {
            logger.error("Error sending registration OTP to {}: {}", recipientEmail, e.getMessage());
            SendEmailResponseDto.builder()
                    .success(false)
                    .statusCode(500)
                    .message(MessageBundle.getMessage("app.email.register.fail"))
                    .build();
        }
    }
}
