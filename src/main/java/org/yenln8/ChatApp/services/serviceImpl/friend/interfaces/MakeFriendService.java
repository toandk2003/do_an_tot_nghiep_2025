package org.yenln8.ChatApp.services.serviceImpl.friend.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;

public interface MakeFriendService {
    BaseResponseDto call(Long receiverId, HttpServletRequest request);
}
