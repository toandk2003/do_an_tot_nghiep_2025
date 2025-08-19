package org.yenln8.ChatApp.services.interfaces;

import org.yenln8.ChatApp.dto.base.BaseResponseDto;

public interface FriendService {
    BaseResponseDto makeFriendRequest(Long receiverId);

    BaseResponseDto acceptFriendRequest();

    BaseResponseDto rejectFriendRequest();

    BaseResponseDto cancelFriendRequest(Long friendRequestId);

    BaseResponseDto getListFriendRequestIMade();

    BaseResponseDto getListFriendRequestIReceived();

    BaseResponseDto removeFriend();

    BaseResponseDto getListFriend();
}
