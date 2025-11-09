package org.yenln8.ChatApp.services.serviceImpl.friend.implement;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.response.RejectFriendResponseDto;
import org.yenln8.ChatApp.entity.Event;
import org.yenln8.ChatApp.entity.FriendRequest;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.EventRepository;
import org.yenln8.ChatApp.repository.FriendRequestRepository;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.RejectFriendRequestService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class RejectFriendRequestServiceImpl implements RejectFriendRequestService {
    private FriendRequestRepository friendRequestRepository;
    private GetFullInfoAboutUserService getFullInfoAboutUserService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public BaseResponseDto call(Long friendRequestId) {
        // Kiem tra friend-request co ton tai + co trang thai pending + deleted = 0
        // Kiem tra ban than co phai la nguoi nhan request k
        CurrentUser currentUser = ContextService.getCurrentUser();

        FriendRequest friendRequest = this.validate(currentUser, friendRequestId);

        FriendRequest friendRequestSaved = this.save(friendRequest);
        User sender = friendRequestSaved.getSender();
        User receiver = friendRequestSaved.getReceiver();

        RejectFriendResponseDto responseDto = RejectFriendResponseDto.builder()
                .id(friendRequestSaved.getId())
                .sender(this.getFullInfoAboutUserService.call(sender))
                .receiver(this.getFullInfoAboutUserService.call(receiver))
                .status(friendRequestSaved.getStatus())
                .sentAt(friendRequestSaved.getCreatedAt())
                .responseAt(friendRequestSaved.getResponsedAt())
                .build();

        var response = BaseResponseDto.builder()
                .success(true)
                .statusCode(200)
                .data(responseDto)
                .message("Reject friend request successfully")
                .eventType(Event.TYPE.REJECT_FRIEND_REQUEST)
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

        return response;
    }

    private FriendRequest save(FriendRequest friendRequest) {
        // Update friend request
        friendRequest.setStatus(FriendRequest.STATUS.REJECTED);
        friendRequest.setResponsedAt(LocalDateTime.now());
        friendRequest.setDeletedAt(LocalDateTime.now());
        friendRequest.setDeleted(friendRequest.getId());

        return friendRequestRepository.save(friendRequest);
    }

    private FriendRequest validate(CurrentUser currentUser, Long friendRequestId) {
        Long userId = currentUser.getId();
        // Kiem tra friend-request co ton tai + co trang thai pending + deleted = 0

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
