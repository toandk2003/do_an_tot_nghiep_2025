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

    /**
     * Generate 8-digit OTP
     *
     * @return 8-digit OTP as String
     */
    public String generateOTP() {
        Random random = new Random();
        int otp = 10000000 + random.nextInt(90000000); // 8 digit OTP (10000000 - 99999999)
        return String.valueOf(otp);
    }

    /**
     * Send OTP email for account registration
     *
     * @param recipientEmail Email address to send OTP to
     * @return SendEmailResponseDto with result status
     */
    @Override
    public SendEmailResponseDto sendOTPChangePassword(String recipientEmail) {
        try {
            // Generate 8-digit OTP
            String otp = generateOTP();

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
            return response ;

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
