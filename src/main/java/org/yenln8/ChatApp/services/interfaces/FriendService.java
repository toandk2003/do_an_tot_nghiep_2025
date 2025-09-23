package org.yenln8.ChatApp.services.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.GetListFriendRequestDto;
import org.yenln8.ChatApp.dto.request.GetListFriendRequestReceivedRequestDto;
import org.yenln8.ChatApp.dto.request.GetListFriendRequestSentRequestDto;

public interface FriendService {
    BaseResponseDto makeFriendRequest(Long receiverId, HttpServletRequest request);

    BaseResponseDto acceptFriendRequest(Long friendRequestId, HttpServletRequest request);

    BaseResponseDto rejectFriendRequest(Long friendRequestId);

    BaseResponseDto cancelFriendRequest(Long friendRequestId);

    BaseResponseDto getListFriendRequestIMade(GetListFriendRequestSentRequestDto from);

    BaseResponseDto getListFriendRequestIReceived(GetListFriendRequestReceivedRequestDto form);

    BaseResponseDto removeFriend(Long friendId);

    BaseResponseDto getListFriend(GetListFriendRequestDto form);
}
