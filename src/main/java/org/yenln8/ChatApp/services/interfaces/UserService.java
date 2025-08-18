package org.yenln8.ChatApp.services.interfaces;

import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.ExploreRequestDto;
import org.yenln8.ChatApp.entity.User;

public interface UserService {
    BaseResponseDto explore(ExploreRequestDto form);
    BaseResponseDto block(ExploreRequestDto form);
    BaseResponseDto unblock(ExploreRequestDto form);
    User getUserActive(Long userId);
}
