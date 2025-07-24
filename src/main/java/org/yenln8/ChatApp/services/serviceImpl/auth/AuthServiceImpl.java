package org.yenln8.ChatApp.services.serviceImpl.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.LoginRequestDto;
import org.yenln8.ChatApp.dto.request.RegisterAccountRequestDto;
import org.yenln8.ChatApp.dto.request.VerifyOtpRegisterRequestDto;
import org.yenln8.ChatApp.services.*;
import org.yenln8.ChatApp.services.serviceImpl.auth.service.LoginService;
import org.yenln8.ChatApp.services.serviceImpl.auth.service.RegisterService;
import org.yenln8.ChatApp.services.serviceImpl.auth.service.VerifyOtpRegisterService;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private LoginService loginService;
    private RegisterService registerService;
    private VerifyOtpRegisterService verifyOtpRegisterService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) {
        return this.loginService.call(loginRequestDto, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto register(RegisterAccountRequestDto form, HttpServletRequest request) throws Exception {return this.registerService.call(form, request);}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto verifyOtpRegister(VerifyOtpRegisterRequestDto form, HttpServletRequest request) throws Exception {
        return verifyOtpRegisterService.call(form, request);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BaseResponseDto changePassword(String email) {
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BaseResponseDto resetPassword(String email) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto logout() {
        return null;
    }
}
