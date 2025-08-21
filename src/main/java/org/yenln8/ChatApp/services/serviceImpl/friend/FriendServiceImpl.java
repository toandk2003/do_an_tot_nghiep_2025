package org.yenln8.ChatApp.services.serviceImpl.friend;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.GetListFriendRequestDto;
import org.yenln8.ChatApp.dto.request.GetListFriendRequestReceivedRequestDto;
import org.yenln8.ChatApp.dto.request.GetListFriendRequestSentRequestDto;
import org.yenln8.ChatApp.services.interfaces.FriendService;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.*;

@Service
@AllArgsConstructor
public class FriendServiceImpl implements FriendService {
    private MakeFriendService makeFriendService;
    private CancelFriendRequestService cancelFriendRequestService;
    private GetListFriendRequestSentService getListFriendRequestSentService;
    private GetListFriendRequestReceivedService getListFriendRequestReceivedService;
    private GetListFriendService getListFriendService;
    private AcceptFriendRequestService acceptFriendRequestService;
    private RejectFriendRequestService rejectFriendRequestService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto makeFriendRequest(Long receiverId) {
        return this.makeFriendService.call(receiverId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto acceptFriendRequest(Long friendRequestId) {
        return this.acceptFriendRequestService.call(friendRequestId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto rejectFriendRequest(Long friendRequestId) {
        return this.rejectFriendRequestService.call(friendRequestId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto cancelFriendRequest(Long friendRequestId) {
        return this.cancelFriendRequestService.call(friendRequestId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto getListFriendRequestIMade(GetListFriendRequestSentRequestDto form) {
        return this.getListFriendRequestSentService.call(form);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto getListFriendRequestIReceived(GetListFriendRequestReceivedRequestDto form) {
        return this.getListFriendRequestReceivedService.call(form);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto removeFriend() {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseDto getListFriend(GetListFriendRequestDto form) {
        return this.getListFriendService.call(form);
    }
}
