package org.yenln8.ChatApp.services.serviceImpl.friend.implement;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.base.PaginationResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.request.GetListFriendRequestReceivedRequestDto;
import org.yenln8.ChatApp.dto.response.GetListFriendRequestReceivedResponseDto;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.FriendRequest;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.FriendRequestRepository;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.GetListFriendRequestReceivedService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class GetListFriendRequestReceivedServiceImpl implements GetListFriendRequestReceivedService {
    private FriendRequestRepository friendRequestRepository;
    private GetFullInfoAboutUserService getFullInfoAboutUserService;

    @Override
    public BaseResponseDto call(GetListFriendRequestReceivedRequestDto form) {
        CurrentUser currentUser = ContextService.getCurrentUser();
        Long userId = currentUser.getId();

        int currentPage = form.getCurrentPage().intValue();
        int pageSize = form.getPageSize().intValue();
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize);

        Page<FriendRequest> friendRequestsSent = this.friendRequestRepository.getFriendRequestsReceived(userId, pageRequest);

        List<FriendRequest> friendRequests = friendRequestsSent.getContent();

        List<GetListFriendRequestReceivedResponseDto> result = friendRequests.stream().map(friendRequest -> {
            User sender = friendRequest.getSender();
            GetProfileResponseDto senderFullInfo = this.getFullInfoAboutUserService.call(sender);

            return GetListFriendRequestReceivedResponseDto.builder()
                    .sender(senderFullInfo)
                    .status(friendRequest.getStatus())
                    .sentAt(friendRequest.getCreatedAt())
                    .build();
        }).toList();

        PaginationResponseDto<FriendRequest, GetListFriendRequestReceivedResponseDto> data = PaginationResponseDto.of(friendRequestsSent, result);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(200)
                .data(data)
                .message("Here are the friend requests you have received.")
                .build();
    }
}
