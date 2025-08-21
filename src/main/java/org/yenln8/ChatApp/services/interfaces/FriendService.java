package org.yenln8.ChatApp.services.interfaces;

import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.GetListFriendRequestDto;
import org.yenln8.ChatApp.dto.request.GetListFriendRequestReceivedRequestDto;
import org.yenln8.ChatApp.dto.request.GetListFriendRequestSentRequestDto;

public interface FriendService {
    BaseResponseDto makeFriendRequest(Long receiverId);

    BaseResponseDto acceptFriendRequest();

    BaseResponseDto rejectFriendRequest();

    BaseResponseDto cancelFriendRequest(Long friendRequestId);

    BaseResponseDto getListFriendRequestIMade(GetListFriendRequestSentRequestDto from);

    BaseResponseDto getListFriendRequestIReceived(GetListFriendRequestReceivedRequestDto form);

    BaseResponseDto removeFriend();

    BaseResponseDto getListFriend(GetListFriendRequestDto form);
}
