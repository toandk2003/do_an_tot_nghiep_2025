package org.yenln8.ChatApp.services.serviceImpl.auth.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.ChangePasswordAccountRequestDto;

public interface ChangePasswordService {
    BaseResponseDto call(ChangePasswordAccountRequestDto form, HttpServletRequest request);
}
