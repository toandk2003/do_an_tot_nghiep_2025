package org.yenln8.ChatApp.controller;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.security.JwtTokenProvider;
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
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/test")
    public ResponseEntity<?> test() throws IllegalAccessException {
//        List<User> users = userRepository.findAll();
        String token = jwtTokenProvider.createToken(1L, "abc@mgila.com", List.of("ADMIN"));
//        Claims claims = jwtTokenProvider.decodeToken(token);
//        System.out.println("token: " + token);
        System.out.println("token: " + token);
        return ResponseEntity.ok(token);
    }
}
