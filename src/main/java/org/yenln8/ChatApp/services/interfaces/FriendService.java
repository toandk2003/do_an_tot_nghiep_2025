package org.yenln8.ChatApp.services.interfaces;

import org.yenln8.ChatApp.dto.base.BaseResponseDto;

public interface FriendService {
    BaseResponseDto makeFriendRequest();
    BaseResponseDto acceptFriendRequest();
    BaseResponseDto rejectFriendRequest();
    BaseResponseDto cancelFriendRequest();
    BaseResponseDto getListFriendRequestIMade();
    BaseResponseDto getListFriendRequestIReceived();

    BaseResponseDto removeFriend();
    BaseResponseDto getListFriend();
}
