package org.yenln8.ChatApp.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.service.EmailService;
import org.yenln8.ChatApp.service.SendOTPChangePasswordService;
import org.yenln8.ChatApp.service.SendOTPRegistrationService;
import org.yenln8.ChatApp.service.SendOTPResetPasswordService;

import java.util.List;

@RestController
@AllArgsConstructor
public class TestController {
    private EmailService emailService;
    private SendOTPRegistrationService sendOTPRegistrationService;
    private SendOTPResetPasswordService sendOTPResetPasswordService;
    private SendOTPChangePasswordService sendOTPChangePasswordService;
    private UserRepository userRepository;

    @GetMapping("/test")
    public ResponseEntity<?> test() throws IllegalAccessException {
        List<User> users = userRepository.findAll();
        throw  new IllegalAccessException();
//        return ResponseEntity.ok(users);
    }
}
