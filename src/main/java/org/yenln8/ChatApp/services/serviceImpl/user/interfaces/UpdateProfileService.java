package org.yenln8.ChatApp.services.serviceImpl.user.interfaces;

import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.UpdateProfileRequestDto;

public interface UpdateProfileService {
    BaseResponseDto call(UpdateProfileRequestDto form);
}
