package org.yenln8.ChatApp.service.serviceImpl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.service.*;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class AuthServiceImpl implements AuthService {
    private EmailService emailService;
    private SendOTPRegistrationService sendOTPRegistrationService;
    private SendOTPResetPasswordService sendOTPResetPasswordService;
    private SendOTPChangePasswordService sendOTPChangePasswordService;
    private UserRepository userRepository;
    @Override
    public BaseResponseDto login(String username, String password) {
        return null;
    }

    @Override
    public BaseResponseDto register(String email) {
        return null;
    }

    @Override
    public BaseResponseDto changePassword(String email) {
        return null;
    }

    @Override
    public BaseResponseDto resetPassword(String email) {
        return null;
    }
}
