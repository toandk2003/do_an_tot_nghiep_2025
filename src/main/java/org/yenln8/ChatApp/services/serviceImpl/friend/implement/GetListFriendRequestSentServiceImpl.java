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
import org.yenln8.ChatApp.dto.request.GetListFriendRequestSentRequestDto;
import org.yenln8.ChatApp.dto.response.GetListFriendRequestSentResponseDto;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.FriendRequest;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.FriendRequestRepository;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.GetListFriendRequestSentService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class GetListFriendRequestSentServiceImpl implements GetListFriendRequestSentService {
    private FriendRequestRepository friendRequestRepository;
    private GetFullInfoAboutUserService getFullInfoAboutUserService;

    @Override
    public BaseResponseDto call(GetListFriendRequestSentRequestDto form) {
        CurrentUser currentUser = ContextService.getCurrentUser();
        Long userId = currentUser.getId();

        int currentPage = form.getCurrentPage().intValue();
        int pageSize = form.getPageSize().intValue();
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize);

        Page<FriendRequest> friendRequestsSent = this.friendRequestRepository.getFriendRequestsSent(userId, pageRequest);
        List<FriendRequest> friendRequests = friendRequestsSent.getContent();
        List<GetListFriendRequestSentResponseDto> result = friendRequests.stream().map(friendRequest -> {
            User receiver = friendRequest.getReceiver();
            GetProfileResponseDto receiverFullInfo = this.getFullInfoAboutUserService.call(receiver);

            return GetListFriendRequestSentResponseDto.builder()
                    .id(friendRequest.getId())
                    .receiver(receiverFullInfo)
                    .status(friendRequest.getStatus())
                    .sentAt(friendRequest.getCreatedAt())
                    .build();
        }).toList();

        PaginationResponseDto<FriendRequest, GetListFriendRequestSentResponseDto> data = PaginationResponseDto.of(friendRequestsSent, result);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(200)
                .data(data)
                .message("Here are your sent friend requests.")
                .build();
    }
}
