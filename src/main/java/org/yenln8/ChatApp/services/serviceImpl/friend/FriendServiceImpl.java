package org.yenln8.ChatApp.services.serviceImpl.friend;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.ExploreRequestDto;
import org.yenln8.ChatApp.services.interfaces.FriendService;
import org.yenln8.ChatApp.services.interfaces.UserService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.ExploreService;

@Service
@AllArgsConstructor
public class FriendServiceImpl implements FriendService {
    @Override
    public BaseResponseDto makeFriendRequest() {
        return null;
    }

    @Override
    public BaseResponseDto acceptFriendRequest() {
        return null;
    }

    @Override
    public BaseResponseDto rejectFriendRequest() {
        return null;
    }

    @Override
    public BaseResponseDto cancelFriendRequest() {
        return null;
    }

    @Override
    public BaseResponseDto getListFriendRequestIMade() {
        return null;
    }

    @Override
    public BaseResponseDto getListFriendRequestIReceived() {
        return null;
    }

    @Override
    public BaseResponseDto removeFriend() {
        return null;
    }

    @Override
    public BaseResponseDto getListFriend() {
        return null;
    }
}
