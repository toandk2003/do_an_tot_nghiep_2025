package org.yenln8.ChatApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.EmailService;
import org.yenln8.ChatApp.security.JwtTokenProvider;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class TestController {
    private JwtTokenProvider jwtTokenProvider;
    private EmailService emailService;
    private RedisTemplate<String, Object> redisTemplate;
    private UserRepository  userRepository;

    @GetMapping("/test")
    public ResponseEntity<?> test() throws IllegalAccessException {
        String token = jwtTokenProvider.createToken(1L, "abc@mgila.com", List.of("ADMIN"));
        return ResponseEntity.ok(token);
    }

    @GetMapping("/tevst-exception")
    public ResponseEntity<?> exceptions(HttpServletRequest request) throws Exception {
        throw new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "AccountPending", "otpId",List.of(2,3,4)));
    }

}
