package org.yenln8.ChatApp.services.serviceImpl.friend.implement;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.response.RejectFriendResponseDto;
import org.yenln8.ChatApp.dto.response.RemoveFriendResponseDto;
import org.yenln8.ChatApp.entity.Friend;
import org.yenln8.ChatApp.entity.FriendRequest;
import org.yenln8.ChatApp.repository.FriendRepository;
import org.yenln8.ChatApp.repository.FriendRequestRepository;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.RejectFriendRequestService;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.RemoveFriendService;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class RemoveFriendServiceImpl implements RemoveFriendService {
    private FriendRepository friendRepository;

    @Override
    public BaseResponseDto call(Long friendId) {
        // Kiem tra friend-request co ton tai + co trang thai pending + deleted = 0
        // Kiem tra ban than co tham gia vao friend nay k

        CurrentUser currentUser = ContextService.getCurrentUser();

        Friend friend = this.validate(currentUser, friendId);

        this.save(friend);

        RemoveFriendResponseDto responseDto = RemoveFriendResponseDto.builder()
                .id(friend.getId())
                .senderId(friend.getUser1().getId())
                .receiverId(friend.getUser2().getId())
                .build();

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(200)
                .data(responseDto)
                .message("Reject friend request successfully")
                .build();
    }

    private void save(Friend friend) {
        // Update friend
        friend.setDeletedAt(LocalDateTime.now());
        friend.setDeleted(friend.getId());

        friendRepository.save(friend);
    }

    private Friend validate(CurrentUser currentUser, Long friendId) {
        Long userId = currentUser.getId();
        // Kiem tra friend co ton tai + deleted = 0

        Friend friend = this.friendRepository.findByIdAndDeletedAtIsNull(friendId)
                .orElseThrow(() -> new IllegalArgumentException(
                MessageBundle.getMessage("error.object.not.found", "Friend", "id", friendId)));

        // Kiem tra ban than co tham gia vao friend nay k
        if (!friend.getUser1().getId().equals(userId) &&  !friend.getUser2().getId().equals(userId)) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.friend.must.not.unfriend"));
        }

        return friend;
    }
}
