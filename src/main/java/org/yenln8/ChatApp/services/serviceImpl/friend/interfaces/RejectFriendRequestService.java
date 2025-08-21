package org.yenln8.ChatApp.services.serviceImpl.friend.interfaces;

import org.yenln8.ChatApp.dto.base.BaseResponseDto;

public interface RejectFriendRequestService {
    BaseResponseDto call(Long friendRequestId);
}
