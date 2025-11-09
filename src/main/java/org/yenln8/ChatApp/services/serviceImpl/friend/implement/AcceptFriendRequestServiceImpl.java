package org.yenln8.ChatApp.services.serviceImpl.friend.implement;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.response.AcceptFriendResponseDto;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.event.synchronize.SynchronizeConversationEvent;
import org.yenln8.ChatApp.repository.EventRepository;
import org.yenln8.ChatApp.repository.FriendRepository;
import org.yenln8.ChatApp.repository.FriendRequestRepository;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.AcceptFriendRequestService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AcceptFriendRequestServiceImpl implements AcceptFriendRequestService {
    @Value("${app.redis.streams.sync-stream}")
    private String syncStream;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private GetFullInfoAboutUserService getFullInfoAboutUserService;

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

        var response = BaseResponseDto.builder()
                .success(true)
                .statusCode(200)
                .data(responseDto)
                .message("Accept friend request successfully.")
                .eventType(Event.TYPE.ACCEPT_FRIEND_REQUEST)
                .build();
        try {
            eventRepository.save(Event.builder()
                    .payload(objectMapper.writeValueAsString(response))
                    .destination("sync-stream")
                    .status(Event.STATUS.WAIT_TO_SEND)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            log.info(e.getMessage());
        }


        try {
            SynchronizeConversationEvent synchronizeConversationEvent = SynchronizeConversationEvent.builder()
                    .participants(List.of(sender.getEmail(), receiver.getEmail()))
                    .type("private")
                    .eventType(Event.TYPE.SYNC_CONVERSATION)
                    .build();

            String body = objectMapper.writeValueAsString(synchronizeConversationEvent);
            log.info("synchronizeConversationDto: {}", body);

            eventRepository.save(Event.builder()
                    .payload(body)
                    .destination(syncStream)
                    .status(Event.STATUS.WAIT_TO_SEND)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());

            //--------------------------------------------------
            //   sent Noti for sender friend request
            Notification notiEvent1 = Notification.builder()
                    .senderType(Notification.SENDER_TYPE.SYSTEM)
                    .receiverEmail(sender.getEmail())
                    .receiverType(Notification.RECEIVER_TYPE.USER)
                    .referenceEmail(receiver.getEmail())
                    .referenceType(Notification.REFERENCE_TYPE.USER)
                    .content(MessageBundle.getMessage("message.notification.accept.friend.sender", receiverFullInfo.getFullName()))
                    .status(Notification.STATUS.NOT_SEEN)
                    .type(Notification.TYPE.ACCEPT_FRIEND_REQUEST)
                    .createdBy(0L)
                    .eventType(Event.TYPE.NOTI)
                    .build();

            eventRepository.save(Event.builder()
                    .payload(objectMapper.writeValueAsString(notiEvent1))
                    .destination(syncStream)
                    .status(Event.STATUS.WAIT_TO_SEND)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());

            //   sent Noti for receiver friend request
            Notification notiEvent2 = Notification.builder()
                    .senderType(Notification.SENDER_TYPE.SYSTEM)
                    .receiverEmail(receiver.getEmail())
                    .receiverType(Notification.RECEIVER_TYPE.USER)
                    .referenceType(Notification.REFERENCE_TYPE.USER)
                    .referenceEmail(sender.getEmail())
                    .content(MessageBundle.getMessage("message.notification.accept.friend.receiver", senderFullInfo.getFullName()))
                    .status(Notification.STATUS.NOT_SEEN)
                    .type(Notification.TYPE.ACCEPT_FRIEND_REQUEST)
                    .createdBy(0L)
                    .eventType(Event.TYPE.NOTI)
                    .build();

            eventRepository.save(Event.builder()
                    .payload(objectMapper.writeValueAsString(notiEvent2))
                    .destination(syncStream)
                    .status(Event.STATUS.WAIT_TO_SEND)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());


        } catch (Exception e) {
            log.error("Sent event fail : {}", e.getMessage());
        }

        return response;
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
