package org.yenln8.ChatApp.services;

import jakarta.servlet.http.HttpServletRequest;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.LoginRequestDto;
import org.yenln8.ChatApp.dto.request.RegisterAccountRequestDto;
import org.yenln8.ChatApp.dto.request.VerifyOtpRegisterRequestDto;

public interface AuthService {
    BaseResponseDto login(LoginRequestDto form, HttpServletRequest request);
    BaseResponseDto register(RegisterAccountRequestDto form, HttpServletRequest request) throws Exception;
    BaseResponseDto verifyOtpRegister(VerifyOtpRegisterRequestDto form, HttpServletRequest request) throws Exception;
    BaseResponseDto changePassword(String email);
    BaseResponseDto resetPassword(String email);
    BaseResponseDto logout();

}
