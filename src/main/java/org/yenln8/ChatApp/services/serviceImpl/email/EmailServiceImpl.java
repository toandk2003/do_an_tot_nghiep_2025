package org.yenln8.ChatApp.services.serviceImpl.email;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.dto.response.SendEmailResponseDto;
import org.yenln8.ChatApp.services.EmailService;
import org.yenln8.ChatApp.services.serviceImpl.email.intefaces.SendEmailService;
import org.yenln8.ChatApp.services.serviceImpl.email.intefaces.SendOTPChangePasswordService;
import org.yenln8.ChatApp.services.serviceImpl.email.intefaces.SendOTPRegistrationService;
import org.yenln8.ChatApp.services.serviceImpl.email.intefaces.SendOTPResetPasswordService;

@AllArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {
    private SendEmailService sendEmailService;
    private SendOTPRegistrationService sendOTPRegistrationService;
    private SendOTPChangePasswordService sendOTPChangePasswordService;
    private SendOTPResetPasswordService sendOTPResetPasswordService;

    @Override
    public SendEmailResponseDto systemSendTo(String recipientEmail, String subject, String content) {
        return this.sendEmailService.call(recipientEmail, subject, content);
    }

    @Override
    public void sendOTPChangePassword(String recipientEmail, String otp) {
        this.sendOTPChangePasswordService.call(recipientEmail, otp);
    }

    @Override
    public void sendOTPRegistration(String recipientEmail, String otp) {
        this.sendOTPRegistrationService.call(recipientEmail, otp);
    }

    @Override
    public void sendOTPResetPassword(String recipientEmail, String otp) {
        this.sendOTPResetPasswordService.call(recipientEmail, otp);

    }
}
