package org.yenln8.ChatApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.dto.request.LoginRequestDto;
import org.yenln8.ChatApp.dto.request.RegisterAccountRequestDto;
import org.yenln8.ChatApp.dto.request.VerifyOtpRegisterRequestDto;
import org.yenln8.ChatApp.services.AuthService;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDto form,HttpServletRequest request) {
        return ResponseEntity.ok(this.authService.login(form, request));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterAccountRequestDto form, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(this.authService.register(form, request));
    }

    @PostMapping("/register/verify-otp")
    public ResponseEntity<?> verifyOtpRegister(@RequestBody @Valid VerifyOtpRegisterRequestDto form, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(this.authService.verifyOtpRegister(form, request));
    }
}
