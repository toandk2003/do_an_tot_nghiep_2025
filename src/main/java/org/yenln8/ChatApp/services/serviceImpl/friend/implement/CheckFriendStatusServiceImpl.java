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
import org.yenln8.ChatApp.dto.request.CheckStatusFriendRequestDto;
import org.yenln8.ChatApp.dto.request.GetListFriendRequestSentRequestDto;
import org.yenln8.ChatApp.dto.response.CheckStatusFriendResponseDto;
import org.yenln8.ChatApp.dto.response.FriendStatusDto;
import org.yenln8.ChatApp.dto.response.GetListFriendRequestSentResponseDto;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.Friend;
import org.yenln8.ChatApp.entity.FriendRequest;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.FriendRepository;
import org.yenln8.ChatApp.repository.FriendRequestRepository;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.GetListFriendRequestSentService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CheckFriendStatusServiceImpl {
    private FriendRequestRepository friendRequestRepository;
    private GetFullInfoAboutUserService getFullInfoAboutUserService;
    private FriendRepository friendRepository;
    private UserRepository userRepository;

    public BaseResponseDto call(CheckStatusFriendRequestDto form) {
        CurrentUser currentUser = ContextService.getCurrentUser();
        List<String> emails = form.getEmails();
        Long userId = currentUser.getId();
        CheckStatusFriendResponseDto response = new CheckStatusFriendResponseDto(new ArrayList<>());

        for (String email : emails) {
            User user = userRepository.findByEmailAndDeletedAtIsNull(email).orElseThrow(() -> new IllegalArgumentException(("NOT FOUND USER WITH email: " + email)));
            boolean isFriend = friendRepository.areFriends(userId, user.getId());
            FriendStatusDto friendStatusDto = FriendStatusDto.builder().userIdInMySql(user.getId()).email(email).build();

            if (isFriend) {
                friendStatusDto.setFriend(true);
            } else {
                friendStatusDto.setFriend(false);
                boolean alreadySentFriendRequest = friendRequestRepository.alreadySentFriendRequest(userId, user.getId());

                if (alreadySentFriendRequest) {
                    friendStatusDto.setNotFriendAndNoSentFriendRequest(false);
                    friendStatusDto.setNotFriendAndSentFriendRequest(true);
                } else {
                    friendStatusDto.setNotFriendAndNoSentFriendRequest(true);
                    friendStatusDto.setNotFriendAndSentFriendRequest(false);
                }
            }

            response.getListFriendStatus().add(friendStatusDto);
        }

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(200)
                .data(response)
                .message("Here are your status of list email you wanna check.")
                .build();
    }
}
