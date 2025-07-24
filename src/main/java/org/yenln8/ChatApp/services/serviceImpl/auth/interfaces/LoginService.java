package org.yenln8.ChatApp.services.serviceImpl.auth.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.LoginRequestDto;

public interface LoginService {
    BaseResponseDto call(LoginRequestDto loginRequestDto, HttpServletRequest request);
}
