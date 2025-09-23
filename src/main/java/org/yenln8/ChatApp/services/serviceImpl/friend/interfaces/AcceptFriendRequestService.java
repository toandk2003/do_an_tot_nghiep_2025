package org.yenln8.ChatApp.services.serviceImpl.friend.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;

public interface AcceptFriendRequestService {
    BaseResponseDto call(Long friendRequestId, HttpServletRequest request);
}
