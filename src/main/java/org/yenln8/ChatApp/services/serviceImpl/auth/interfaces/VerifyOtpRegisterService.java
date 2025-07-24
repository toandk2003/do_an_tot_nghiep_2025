package org.yenln8.ChatApp.services.serviceImpl.auth.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.VerifyOtpRequestDto;

public interface VerifyOtpRegisterService {
    BaseResponseDto call(VerifyOtpRequestDto form, HttpServletRequest request) throws Exception;
}
