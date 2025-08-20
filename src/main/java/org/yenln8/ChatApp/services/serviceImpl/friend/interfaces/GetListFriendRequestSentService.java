package org.yenln8.ChatApp.services.serviceImpl.friend.interfaces;

import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.GetListFriendRequestSentRequestDto;

public interface GetListFriendRequestSentService {
    BaseResponseDto call(GetListFriendRequestSentRequestDto form);
}
