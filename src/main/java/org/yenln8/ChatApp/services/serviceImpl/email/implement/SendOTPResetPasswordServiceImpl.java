package org.yenln8.ChatApp.services.serviceImpl.email.implement;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.response.SendEmailResponseDto;
import org.yenln8.ChatApp.services.serviceImpl.email.intefaces.SendOTPResetPasswordService;

@AllArgsConstructor
@Service
public class SendOTPResetPasswordServiceImpl implements SendOTPResetPasswordService {
    private static final Logger logger = LoggerFactory.getLogger(SendOTPResetPasswordServiceImpl.class);
    private SendEmailServiceImpl sendEmailService;

    @Override
    public void call(String recipientEmail, String otp) {
        try {
            // Email subject
            String subject = MessageBundle.getMessage("app.email.reset.password.otp.subject");

            // Email content template
            String content = MessageBundle.getMessage("app.email.reset.password.otp.content", otp);
            logger.info(content);
            // Send email using EmailService
            SendEmailResponseDto response = sendEmailService.call(recipientEmail, subject, content);
            logger.info("OTP Reset Password OTP Verification Response: " + response);
            if (response.getSuccess()) {
                logger.info("Reset Password  OTP sent successfully to: {}", recipientEmail);
            } else {
                logger.error("Failed to send Reset Password OTP to: {}", recipientEmail);
            }
            response.setMessage(MessageBundle.getMessage("app.email.reset.password.success"));

        } catch (Exception e) {
            logger.error("Error sending Reset Password  OTP to {}: {}", recipientEmail, e.getMessage());
            SendEmailResponseDto.builder()
                    .success(false)
                    .statusCode(500)
                    .message(MessageBundle.getMessage("app.email.reset.password.fail"))
                    .build();
        }
    }
}
