package org.yenln8.ChatApp.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.dto.SendEmailResponseDto;
import org.yenln8.ChatApp.service.EmailService;
import org.yenln8.ChatApp.service.SendOTPChangePasswordService;
import org.yenln8.ChatApp.service.SendOTPRegistrationService;
import org.yenln8.ChatApp.service.SendOTPResetPasswordService;

@RestController
@AllArgsConstructor
public class TestController {
    private EmailService emailService;
    private SendOTPRegistrationService sendOTPRegistrationService;
    private SendOTPResetPasswordService sendOTPResetPasswordService;
    private SendOTPChangePasswordService sendOTPChangePasswordService;

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        SendEmailResponseDto sendEmailResponseDto = this.sendOTPRegistrationService.sendOTPRegistration("dieutuyetnguyenthi@gmail.com");
        System.out.println(sendEmailResponseDto);

        SendEmailResponseDto sendOTPChangePassword = this.sendOTPChangePasswordService.sendOTPChangePassword("dieutuyetnguyenthi@gmail.com");
        System.out.println(sendOTPChangePassword);

        SendEmailResponseDto sendEmailResponseDto1 = this.sendOTPResetPasswordService.sendOTPResetPassword("dieutuyetnguyenthi@gmail.com");
        System.out.println(sendEmailResponseDto1);

        return ResponseEntity.ok(sendOTPChangePassword);
    }
}
