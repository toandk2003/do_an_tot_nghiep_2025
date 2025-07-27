package org.yenln8.ChatApp.services.serviceImpl.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.*;
import org.yenln8.ChatApp.services.*;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.*;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private LoginService loginService;
    private RegisterService registerService;
    private VerifyOtpRegisterService verifyOtpRegisterService;
    private ChangePasswordService changePasswordService;
    private VerifyChangePasswordService verifyChangePasswordService;
    private ResetPasswordService resetPasswordService;
    private VerifyResetPasswordService verifyResetPasswordService;
    private LogOutService logOutService;
    private GetProfileService getProfileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) {
        return this.loginService.call(loginRequestDto, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto register(RegisterAccountRequestDto form, HttpServletRequest request) throws Exception {
        return this.registerService.call(form, request);}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto verifyOtpRegister(VerifyOtpRequestDto form, HttpServletRequest request) throws Exception {
        return verifyOtpRegisterService.call(form, request);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BaseResponseDto changePassword(ChangePasswordAccountRequestDto form, HttpServletRequest request) {
        return this.changePasswordService.call(form,request);
    }

    @Override
    public BaseResponseDto verifyChangePassword(VerifyOtpRequestDto form, HttpServletRequest request) throws Exception {
        return this.verifyChangePasswordService.call(form,request);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BaseResponseDto resetPassword(ResetPasswordAccountRequestDto form, HttpServletRequest request) {
        return this.resetPasswordService.call(form,request);
    }

    @Override
    public BaseResponseDto verifyOtpResetPassword(VerifyOtpResetPasswordRequestDto form, HttpServletRequest request) throws Exception {
        return this.verifyResetPasswordService.call(form,request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto logout(HttpServletRequest request) {
        return this.logOutService.call(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto getProfile(HttpServletRequest request) {
        return this.getProfileService.call(request);
    }
}
