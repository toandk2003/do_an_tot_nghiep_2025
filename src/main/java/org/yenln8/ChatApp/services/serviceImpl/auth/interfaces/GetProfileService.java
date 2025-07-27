package org.yenln8.ChatApp.services.serviceImpl.auth.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;

public interface GetProfileService {
    BaseResponseDto call(HttpServletRequest request);
}
