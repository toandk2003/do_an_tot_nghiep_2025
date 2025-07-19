package org.yenln8.ChatApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.services.EmailService;
import org.yenln8.ChatApp.security.JwtTokenProvider;


import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
public class TestController {
    private JwtTokenProvider jwtTokenProvider;
    private EmailService emailService;
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/test")
    public ResponseEntity<?> test() throws IllegalAccessException {
        String token = jwtTokenProvider.createToken(1L, "abc@mgila.com", List.of("ADMIN"));
        return ResponseEntity.ok(token);
    }

    @GetMapping("/tevst-exception")
    public ResponseEntity<?> exceptions(HttpServletRequest request) throws IllegalAccessException {
//        emailService.systemSendTo("ddfdfdfdfddfffffffsaaaa@gmail.com", "verrify", "abccc");
//        System.out.println(Network.getUserIP(request));

        redisTemplate.opsForValue().set("lll", new int[]{1, 2, 3});
//        System.out.println(redisTemplate.opsForValue().get("exception"));
        return ResponseEntity.ok(1);
    }

}
