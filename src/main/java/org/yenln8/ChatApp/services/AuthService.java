package org.yenln8.ChatApp.services;

import jakarta.servlet.http.HttpServletRequest;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.*;

public interface AuthService {
    BaseResponseDto login(LoginRequestDto form, HttpServletRequest request);

    BaseResponseDto register(RegisterAccountRequestDto form, HttpServletRequest request) throws Exception;

    BaseResponseDto verifyOtpRegister(VerifyOtpRequestDto form, HttpServletRequest request) throws Exception;

    BaseResponseDto changePassword(ChangePasswordAccountRequestDto form, HttpServletRequest request) throws Exception;

    BaseResponseDto verifyChangePassword(VerifyOtpRequestDto form, HttpServletRequest request) throws Exception;

    BaseResponseDto resetPassword(ResetPasswordAccountRequestDto form, HttpServletRequest request);

    BaseResponseDto verifyOtpResetPassword(VerifyOtpResetPasswordRequestDto form, HttpServletRequest request) throws Exception;

    BaseResponseDto logout(HttpServletRequest request);

    BaseResponseDto getProfile(HttpServletRequest request);


}
