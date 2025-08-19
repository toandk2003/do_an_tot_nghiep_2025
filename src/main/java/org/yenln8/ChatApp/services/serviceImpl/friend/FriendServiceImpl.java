package org.yenln8.ChatApp.services.serviceImpl.friend;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.services.interfaces.FriendService;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.CancelFriendRequestService;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.MakeFriendService;

@Service
@AllArgsConstructor
public class FriendServiceImpl implements FriendService {
    private MakeFriendService makeFriendService;
    private CancelFriendRequestService cancelFriendRequestService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto makeFriendRequest(Long receiverId) {
        return this.makeFriendService.call(receiverId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto acceptFriendRequest() {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto rejectFriendRequest() {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto cancelFriendRequest(Long friendRequestId) {
        return this.cancelFriendRequestService.call(friendRequestId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto getListFriendRequestIMade() {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto getListFriendRequestIReceived() {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto removeFriend() {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto getListFriend() {
        return null;
    }
}
