package org.yenln8.ChatApp.services.serviceImpl.auth.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.VerifyOtpRequestDto;
import org.yenln8.ChatApp.dto.request.VerifyOtpResetPasswordRequestDto;

public interface VerifyResetPasswordService {
    BaseResponseDto call(VerifyOtpResetPasswordRequestDto form, HttpServletRequest request);
}
