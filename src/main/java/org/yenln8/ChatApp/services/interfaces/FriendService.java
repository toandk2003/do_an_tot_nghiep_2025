package org.yenln8.ChatApp.services.interfaces;

import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.MakeFriendRequestDto;

public interface FriendService {
    BaseResponseDto makeFriendRequest(Long receiverId);
    BaseResponseDto acceptFriendRequest();
    BaseResponseDto rejectFriendRequest();
    BaseResponseDto cancelFriendRequest();
    BaseResponseDto getListFriendRequestIMade();
    BaseResponseDto getListFriendRequestIReceived();

    BaseResponseDto removeFriend();
    BaseResponseDto getListFriend();
}
