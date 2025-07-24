package org.yenln8.ChatApp.services.serviceImpl.auth.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.RegisterAccountRequestDto;

public interface RegisterService {
    BaseResponseDto call(RegisterAccountRequestDto form, HttpServletRequest request) throws Exception;
}
