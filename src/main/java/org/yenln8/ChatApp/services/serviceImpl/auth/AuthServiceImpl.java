package org.yenln8.ChatApp.services.serviceImpl.auth;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.LoginRequestDto;
import org.yenln8.ChatApp.services.*;
import org.yenln8.ChatApp.services.serviceImpl.auth.service.LoginService;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private LoginService loginService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto login(LoginRequestDto loginRequestDto) {
        return this.loginService.call(loginRequestDto);
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

    @Override
    public BaseResponseDto logout() {
        return null;
    }
}
