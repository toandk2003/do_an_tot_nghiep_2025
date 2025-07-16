package org.yenln8.ChatApp.services;

import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.LoginRequestDto;

public interface AuthService {
    BaseResponseDto login(LoginRequestDto form);
    BaseResponseDto register(String email);
    BaseResponseDto changePassword(String email);
    BaseResponseDto resetPassword(String email);
    BaseResponseDto logout();

}
