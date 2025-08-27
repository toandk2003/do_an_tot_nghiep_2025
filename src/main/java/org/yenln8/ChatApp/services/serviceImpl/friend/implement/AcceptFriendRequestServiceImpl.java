package org.yenln8.ChatApp.services.serviceImpl.friend.implement;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.response.AcceptFriendResponseDto;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.Friend;
import org.yenln8.ChatApp.entity.FriendRequest;
import org.yenln8.ChatApp.entity.Notification;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.FriendRepository;
import org.yenln8.ChatApp.repository.FriendRequestRepository;
import org.yenln8.ChatApp.repository.NotificationRepository;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.AcceptFriendRequestService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class AcceptFriendRequestServiceImpl implements AcceptFriendRequestService {
    private FriendRequestRepository friendRequestRepository;
    private FriendRepository friendRepository;
    private GetFullInfoAboutUserService getFullInfoAboutUserService;
    private NotificationRepository notificationRepository;

    @Override
    public BaseResponseDto call(Long friendRequestId) {
        // Kiem tra friend-request co ton tai + co trang thai pending + deleted = 0
        // Kiem tra ban than co phai la nguoi nhan request k

        CurrentUser currentUser = ContextService.getCurrentUser();

        FriendRequest friendRequest = this.validate(currentUser, friendRequestId);

        FriendRequest friendRequestSaved = this.save(friendRequest);
        User sender = friendRequestSaved.getSender();
        User receiver = friendRequestSaved.getReceiver();

        GetProfileResponseDto senderFullInfo = this.getFullInfoAboutUserService.call(sender);
        GetProfileResponseDto receiverFullInfo = this.getFullInfoAboutUserService.call(receiver);

        AcceptFriendResponseDto responseDto = AcceptFriendResponseDto.builder()
                .id(friendRequestSaved.getId())
                .sender(senderFullInfo)
                .receiver(receiverFullInfo)
                .status(friendRequestSaved.getStatus())
                .sentAt(friendRequestSaved.getCreatedAt())
                .responseAt(friendRequestSaved.getResponsedAt())
                .build();
        //sent Noti
//        this.notificationRepository.save(Notification.builder()
//                        .senderId(0L)
//                        .senderType(Notification.SENDER_TYPE.SYSTEM)
//                        .receiverId(sender.getId())
//                        .receiverType(Notification.RECEIVER_TYPE.USER)
//                                .referenceId()
//                build());

        // TODO send noti to 2 user, remember do for auto accept too
        return BaseResponseDto.builder()
                .success(true)
                .statusCode(200)
                .data(responseDto)
                .message("Accept friend request successfully.")
                .build();
    }

    private FriendRequest save(FriendRequest friendRequest) {
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

        this.friendRepository.save(friendToSave);

        return friendRequest;
    }

    private FriendRequest validate(CurrentUser currentUser, Long friendRequestId) {
        Long userId = currentUser.getId();

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
