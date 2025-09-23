package org.yenln8.ChatApp.services.serviceImpl.friend.implement;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.ApiClient;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.common.util.Network;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.response.AcceptFriendResponseDto;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.dto.synchronize.SynchronizeConversationDto;
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
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AcceptFriendRequestServiceImpl implements AcceptFriendRequestService {
    @Value("${url.synchronize.chat-service}")
    private String chatServiceUrl;
    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private GetFullInfoAboutUserService getFullInfoAboutUserService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ApiClient apiClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public BaseResponseDto call(Long friendRequestId, HttpServletRequest request) {
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

        //   sent Noti for sender friend request
        this.notificationRepository.save(Notification.builder()
                .senderType(Notification.SENDER_TYPE.SYSTEM)
                .receiverId(sender.getId())
                .receiverType(Notification.RECEIVER_TYPE.USER)
                .referenceId(receiver.getId())
                .referenceType(Notification.REFERENCE_TYPE.USER)
                .content(MessageBundle.getMessage("message.notification.accept.friend.sender", receiverFullInfo.getFullName()))
                .status(Notification.STATUS.NOT_SEEN)
                .type(Notification.TYPE.ACCEPT_FRIEND_REQUEST)
                .createdBy(0L)
                .build());

        try{
            SynchronizeConversationDto synchronizeConversationDto = SynchronizeConversationDto.builder()
                    .participants(List.of(sender.getId(), receiver.getId()))
                    .type("private")
                    .build();

            String body =  objectMapper.writeValueAsString(synchronizeConversationDto);
            log.info("synchronizeConversationDto: {}", body);
            apiClient.callPostExternalApi(chatServiceUrl + "/synchronize/conversations/private", body, Network.getTokenFromRequest(request));
        }
        catch (Exception e){
            log.error("SynchronizeUserDto: {}", e.getMessage());
        }

        //   sent Noti for receiver friend request
        this.notificationRepository.save(Notification.builder()
                .senderType(Notification.SENDER_TYPE.SYSTEM)
                .receiverId(receiver.getId())
                .receiverType(Notification.RECEIVER_TYPE.USER)
                .referenceType(Notification.REFERENCE_TYPE.USER)
                .referenceId(sender.getId())
                .content(MessageBundle.getMessage("message.notification.accept.friend.receiver", senderFullInfo.getFullName()))
                .status(Notification.STATUS.NOT_SEEN)
                .type(Notification.TYPE.ACCEPT_FRIEND_REQUEST)
                .createdBy(0L)
                .build());

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
