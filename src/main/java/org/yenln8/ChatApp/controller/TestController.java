package org.yenln8.ChatApp.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.security.JwtTokenProvider;
import org.yenln8.ChatApp.services.EmailService;
import org.yenln8.ChatApp.services.SendOTPChangePasswordService;
import org.yenln8.ChatApp.services.SendOTPRegistrationService;
import org.yenln8.ChatApp.services.SendOTPResetPasswordService;

import java.util.List;

@RestController
@AllArgsConstructor
public class TestController {
    private EmailService emailService;
    private SendOTPRegistrationService sendOTPRegistrationService;
    private SendOTPResetPasswordService sendOTPResetPasswordService;
    private SendOTPChangePasswordService sendOTPChangePasswordService;
    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/test")
    public ResponseEntity<?> test() throws IllegalAccessException {
        String token = jwtTokenProvider.createToken(1L, "abc@mgila.com", List.of("ADMIN"));
        return ResponseEntity.ok(token);
    }

    @GetMapping("/tevst-exception")
    public ResponseEntity<?> exceptions() throws IllegalAccessException {
//        List<User> users = userRepository.findAll();
//        String token = jwtTokenProvider.createToken(1L, "abc@mgila.com", List.of("ADMIN"));
//        Claims claims = jwtTokenProvider.decodeToken(token);
//        System.out.println("token: " + token);
//        System.out.println("token: " + token);
        return ResponseEntity.ok(1);
    }
}
