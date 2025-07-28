package org.yenln8.ChatApp.services.serviceImpl.auth.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.ChangePasswordAccountRequestDto;
import org.yenln8.ChatApp.dto.request.OnBoardingRequestDto;

public interface OnBoardingService {
    BaseResponseDto call(OnBoardingRequestDto form, HttpServletRequest request);
}
