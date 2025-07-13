package org.yenln8.ChatApp.service;

import org.yenln8.ChatApp.dto.base.BaseResponseDto;

public interface AuthService {
    BaseResponseDto login(String username, String password);
    BaseResponseDto register(String email);
    BaseResponseDto changePassword(String email);
    BaseResponseDto resetPassword(String email);
}
