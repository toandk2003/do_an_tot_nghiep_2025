package org.yenln8.ChatApp.services.serviceImpl.friend.implement;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.response.CancelFriendResponseDto;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.FriendRequest;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.FriendRequestRepository;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.CancelFriendRequestService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class CancelFriendRequestServiceImpl implements CancelFriendRequestService {
    private FriendRequestRepository friendRequestRepository;
    private GetFullInfoAboutUserService getFullInfoAboutUserService;

    @Override
    public BaseResponseDto call(Long friendRequestId) {
        // Kiem tra friend-request co ton tai + co trang thai pending + deleted = 0
        // Kiem tra ban than co phai la nguoi gui request k
        CurrentUser currentUser = ContextService.getCurrentUser();

        FriendRequest friendRequest = this.validate(currentUser, friendRequestId);

        FriendRequest friendRequestSaved = this.save(friendRequest);

        User sender = friendRequestSaved.getSender();
        User receiver = friendRequestSaved.getReceiver();

        GetProfileResponseDto senderFullInfo = this.getFullInfoAboutUserService.call(sender);
        GetProfileResponseDto receiverFullInfo = this.getFullInfoAboutUserService.call(receiver);

        CancelFriendResponseDto responseDto = CancelFriendResponseDto.builder()
                .id(friendRequestSaved.getId())
                .sender(senderFullInfo)
                .receiver(receiverFullInfo)
                .status(friendRequestSaved.getStatus())
                .sentAt(friendRequestSaved.getCreatedAt())
                .build();

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(200)
                .data(responseDto)
                .message("Cancel friend request successfully")
                .build();
    }

    private FriendRequest save(FriendRequest friendRequest) {
        // Update friend request
        friendRequest.setStatus(FriendRequest.STATUS.CANCEL);
        friendRequest.setResponsedAt(LocalDateTime.now());
        friendRequest.setDeletedAt(LocalDateTime.now());
        friendRequest.setDeleted(friendRequest.getId());

        return friendRequestRepository.save(friendRequest);
    }

    private FriendRequest validate(CurrentUser currentUser, Long friendRequestId) {
        Long userId = currentUser.getId();
        // Kiem tra friend-request co ton tai + co trang thai pending + deleted = 0 + co phai cua current user khong

        FriendRequest friendRequest = this.friendRequestRepository.findByIdAndStatusAndDeletedAtIsNull(
                friendRequestId,
                FriendRequest.STATUS.PENDING
        ).orElseThrow(() -> new IllegalArgumentException(
                MessageBundle.getMessage("error.object.not.found", "FriendRequest", "id", friendRequestId)));

        // Kiem tra ban than co phai la nguoi gui request k
        if (!friendRequest.getSender().getId().equals(userId)) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.friend.is.not.owner.request"));
        }

        return friendRequest;
    }
}
