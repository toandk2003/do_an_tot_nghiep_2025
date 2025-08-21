package org.yenln8.ChatApp.services.serviceImpl.friend.implement;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.response.CancelFriendResponseDto;
import org.yenln8.ChatApp.entity.FriendRequest;
import org.yenln8.ChatApp.repository.FriendRequestRepository;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.CancelFriendRequestService;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.RejectFriendRequestService;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class RejectFriendRequestServiceImpl implements RejectFriendRequestService {
    private FriendRequestRepository friendRequestRepository;

    @Override
    public BaseResponseDto call(Long friendRequestId) {
        // Kiem tra friend-request co ton tai + co trang thai pending + deleted = 0 + co phai cua current user khong
        CurrentUser currentUser = ContextService.getCurrentUser();

        FriendRequest friendRequest = this.validate(currentUser, friendRequestId);

        this.save(friendRequest);

        CancelFriendResponseDto responseDto = CancelFriendResponseDto.builder()
                .id(friendRequest.getId())
                .senderId(friendRequest.getSender().getId())
                .receiverId(friendRequest.getReceiver().getId())
                .status(friendRequest.getStatus())
                .build();

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(200)
                .data(responseDto)
                .message("Reject friend request successfully")
                .build();
    }

    private void save(FriendRequest friendRequest) {
        // Update friend request
        friendRequest.setStatus(FriendRequest.STATUS.REJECTED);
        friendRequest.setDeletedAt(LocalDateTime.now());
        friendRequest.setDeleted(friendRequest.getId());

        friendRequestRepository.save(friendRequest);
    }

    private FriendRequest validate(CurrentUser currentUser, Long friendRequestId) {
        Long userId = currentUser.getId();
        // Kiem tra friend-request co ton tai + co trang thai pending + deleted = 0 + co phai cua current user khong

        FriendRequest friendRequest = this.friendRequestRepository.findByIdAndStatusAndDeletedAtIsNull(
                friendRequestId,
                FriendRequest.STATUS.PENDING
        ).orElseThrow(() -> new IllegalArgumentException(
                MessageBundle.getMessage("error.object.not.found", "FriendRequest", "id", friendRequestId)));

        if (!friendRequest.getSender().getId().equals(userId)) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.friend.is.not.owner.request"));
        }

        return friendRequest;
    }
}
