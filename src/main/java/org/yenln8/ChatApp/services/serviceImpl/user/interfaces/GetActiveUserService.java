package org.yenln8.ChatApp.services.serviceImpl.user.interfaces;

import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.ExploreRequestDto;
import org.yenln8.ChatApp.entity.User;

public interface GetActiveUserService {
    User call(Long userId);
}
