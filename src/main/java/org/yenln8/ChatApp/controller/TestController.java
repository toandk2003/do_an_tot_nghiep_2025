package org.yenln8.ChatApp.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.security.JwtTokenProvider;

import java.util.List;

@RestController
@AllArgsConstructor
public class TestController {
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/test")
    public ResponseEntity<?> test() throws IllegalAccessException {
        String token = jwtTokenProvider.createToken(1L, "abc@mgila.com", List.of("ADMIN"));
        return ResponseEntity.ok(token);
    }

    @GetMapping("/tevst-exception")
    public ResponseEntity<?> exceptions() throws IllegalAccessException {
        return ResponseEntity.ok(1);
    }
}
