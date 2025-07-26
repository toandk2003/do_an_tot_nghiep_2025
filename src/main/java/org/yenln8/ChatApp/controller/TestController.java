package org.yenln8.ChatApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.dto.request.LoginRequestDto;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.EmailService;
import org.yenln8.ChatApp.security.JwtTokenProvider;
import org.yenln8.ChatApp.common.util.RedisService;

@RestController
@AllArgsConstructor
public class TestController {
    private JwtTokenProvider jwtTokenProvider;
    private EmailService emailService;
    private RedisService redisService;
    private UserRepository  userRepository;

    @GetMapping("/test")
    public ResponseEntity<?> test() throws IllegalAccessException {
        return  ResponseEntity.ok(1);
    }

    @GetMapping("/test-need-token")
    public ResponseEntity<?> test2() throws IllegalAccessException {
        return  ResponseEntity.ok(1);
    }

}
