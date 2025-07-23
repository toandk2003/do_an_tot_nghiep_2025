package org.yenln8.ChatApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.redis.BlackListLoginDto;
import org.yenln8.ChatApp.dto.request.LoginRequestDto;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.EmailService;
import org.yenln8.ChatApp.security.JwtTokenProvider;
import org.yenln8.ChatApp.services.RedisService;
import org.yenln8.ChatApp.services.serviceImpl.auth.service.LoginService;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class TestController {
    private JwtTokenProvider jwtTokenProvider;
    private EmailService emailService;
    private RedisService redisService;
    private UserRepository  userRepository;

    @GetMapping("/test")
    public ResponseEntity<?> test() throws IllegalAccessException {
//        String token = jwtTokenProvider.createToken(1L, "abc@mgila.com", List.of("ADMIN"));
//        return ResponseEntity.ok(token);
        LoginRequestDto loginRequestDto = LoginRequestDto.builder().email("sadsdasd").build();
        loginRequestDto.setEmail("abc@gmail.com");
        redisService.setKeyInMinutes(loginRequestDto.getEmail(), "ABCCCCC",1);
        String val = redisService.getKey(loginRequestDto.getEmail(), String.class);
        System.out.println(">>> Value in Redis: " + val);
        return ResponseEntity.ok(val);
//        return  ResponseEntity.ok().build();
    }

    @GetMapping("/tevst-exception")
    public ResponseEntity<?> exceptions(HttpServletRequest request) throws Exception {
        System.out.println(this.redisService.getKey(RedisService.LAST_ONLINE_PREFIX + "ngocyenptit153@gmail.com", Long.class));
        return ResponseEntity.ok(1);
//        throw new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "AccountPending", "otpId",List.of(2,3,4)));
    }

}
