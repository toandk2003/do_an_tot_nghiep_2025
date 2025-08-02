package org.yenln8.ChatApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yenln8.ChatApp.dto.request.*;
import org.yenln8.ChatApp.services.interfaces.AuthService;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDto form, HttpServletRequest request) {
        return ResponseEntity.ok(this.authService.login(form, request));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterAccountRequestDto form, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(this.authService.register(form, request));
    }

    @PostMapping("/signup/verify-otp")
    public ResponseEntity<?> verifyOtpRegister(@RequestBody @Valid VerifyOtpRequestDto form, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(this.authService.verifyOtpRegister(form, request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordAccountRequestDto form, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(this.authService.resetPassword(form, request));
    }

    @PostMapping("/reset-password/verify-otp")
    public ResponseEntity<?> verifyOtpResetPassword(@RequestBody @Valid VerifyOtpResetPasswordRequestDto form, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(this.authService.verifyOtpResetPassword(form, request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordAccountRequestDto form, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(this.authService.changePassword(form, request));
    }

    @PostMapping("/change-password/verify-otp")
    public ResponseEntity<?> verifyOtpChangePassword(@RequestBody @Valid VerifyOtpRequestDto form, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(this.authService.verifyChangePassword(form, request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logOut(HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(this.authService.logout(request));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(this.authService.getProfile(request));
    }

    @PutMapping("/onboarding")
    public ResponseEntity<?> onBoarding(@RequestBody @Valid OnBoardingRequestDto form, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(this.authService.onBoarding(form, request));
    }
}
