package org.yenln8.ChatApp.services.serviceImpl.friend.implement;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.response.AcceptFriendResponseDto;
import org.yenln8.ChatApp.entity.Friend;
import org.yenln8.ChatApp.entity.FriendRequest;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.FriendRepository;
import org.yenln8.ChatApp.repository.FriendRequestRepository;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.AcceptFriendRequestService;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class AcceptFriendRequestServiceImpl implements AcceptFriendRequestService {
    private FriendRequestRepository friendRequestRepository;
    private FriendRepository friendRepository;


    @Override
    public BaseResponseDto call(Long friendRequestId) {
        // Kiem tra friend-request co ton tai + co trang thai pending + deleted = 0 + co phai cua current user khong
        // Kiem tra ban than co phai la nguoi nhan request k

        CurrentUser currentUser = ContextService.getCurrentUser();

        FriendRequest friendRequest = this.validate(currentUser, friendRequestId);

        Friend friend = this.save(friendRequest);

        AcceptFriendResponseDto responseDto = AcceptFriendResponseDto.builder()
                .id(friend.getId())
                .senderId(friend.getUser1().getId())
                .receiverId(friend.getUser2().getId())
                .build();

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(200)
                .data(responseDto)
                .message("Accept friend request successfully")
                .build();
    }

    private Friend save(FriendRequest friendRequest) {
        // Update friend request
        friendRequest.setStatus(FriendRequest.STATUS.ACCEPTED);
        friendRequest.setResponsedAt(LocalDateTime.now());
        friendRequest.setDeletedAt(LocalDateTime.now());
        friendRequest.setDeleted(friendRequest.getId());

        friendRequestRepository.save(friendRequest);

        User sender = friendRequest.getSender();
        User receiver = friendRequest.getReceiver();

        Friend friendToSave = Friend.builder()
                .user1(sender)
                .user2(receiver)
                .build();

        return this.friendRepository.save(friendToSave);
    }

    private FriendRequest validate(CurrentUser currentUser, Long friendRequestId) {
        Long userId = currentUser.getId();
        // Kiem tra friend-request co ton tai + co trang thai pending + deleted = 0 + co phai cua current user khong

        FriendRequest friendRequest = this.friendRequestRepository.findByIdAndStatusAndDeletedAtIsNull(
                friendRequestId,
                FriendRequest.STATUS.PENDING
        ).orElseThrow(() -> new IllegalArgumentException(
                MessageBundle.getMessage("error.object.not.found", "FriendRequest", "id", friendRequestId)));

        // Kiem tra ban than co phai la nguoi nhan request k
        if (!friendRequest.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.friend.is.not.receiver.request"));
        }

        return friendRequest;
    }
}
