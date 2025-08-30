package org.yenln8.ChatApp.services.serviceImpl.friend.implement;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.constant.FriendConstant;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.response.MakeFriendResponseDto;
import org.yenln8.ChatApp.entity.Friend;
import org.yenln8.ChatApp.entity.FriendRequest;
import org.yenln8.ChatApp.entity.Notification;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.BlockRepository;
import org.yenln8.ChatApp.repository.FriendRepository;
import org.yenln8.ChatApp.repository.FriendRequestRepository;
import org.yenln8.ChatApp.repository.NotificationRepository;
import org.yenln8.ChatApp.services.interfaces.UserService;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.MakeFriendService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class MakeFriendServiceImpl implements MakeFriendService {

    private UserService userService;
    private FriendRepository friendRepository;
    private BlockRepository blockRepository;
    private FriendRequestRepository friendRequestRepository;
    private GetFullInfoAboutUserService getFullInfoAboutUserService;
    private NotificationRepository notificationRepository;

    @Override
    public BaseResponseDto call(Long receiverId) {
        // Kiem tra tai khoan cua 2 nguoi co ACTIVE khong
        // Kiem tra co phai gui ket ban cho chinh minh khong
        // Kiem tra 2 nguoi da la ban hay chua
        // Kiem tra 2 nguoi co block nhau khong
        // Kiem tra 2 nguoi co vo tinh gui ket ban cho nhau khong, neu co thi dong y luon
        // Kiem tra gioi han ban be cua 2 nguoi co thoa man khong
        // Kiem tra da ton tai friend-request chua
        // Kiem tra so luong friend-request cua it nhat 1 trong 2 nguoi co dat gioi han khong

        CurrentUser currentUser = ContextService.getCurrentUser();

        List<User> senderAndReceiver = this.validate(currentUser, receiverId);

        User sender = senderAndReceiver.get(0);
        User receiver = senderAndReceiver.get(1);

        FriendRequest friendRequestSaved = this.save(sender, receiver);

        MakeFriendResponseDto data = MakeFriendResponseDto.builder()
                .id(friendRequestSaved.getId())
                .sender(this.getFullInfoAboutUserService.call(sender))
                .receiver(this.getFullInfoAboutUserService.call(receiver))
                .sentAt(friendRequestSaved.getCreatedAt())
                .responseAt(friendRequestSaved.getResponsedAt())
                .status(friendRequestSaved.getStatus())
                .autoAccepted(friendRequestSaved.getStatus().equals(FriendRequest.STATUS.ACCEPTED))
                .build();

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(200)
                .data(data)
                .message("Send friend request successfully")
                .build();
    }

    private FriendRequest save(User sender, User receiver) {

        // Kiem tra 2 nguoi co vo tinh gui ket ban cho nhau khong, neu co thi dong y luon
        if (this.friendRequestRepository.alreadySentFriendRequest(receiver.getId(), sender.getId())) {
            // accept friend
            // Insert Friend
            Friend friendToSave = Friend.builder()
                    .user1(receiver)
                    .user2(sender)
                    .build();
            this.friendRepository.save(friendToSave);

            // Update friend request
            FriendRequest friendRequest = this.friendRequestRepository.getFriendRequestBetweenTwoUser(receiver.getId(), sender.getId());
            friendRequest.setStatus(FriendRequest.STATUS.ACCEPTED);
            friendRequest.setResponsedAt(LocalDateTime.now());
            friendRequest.setDeletedAt(LocalDateTime.now());
            friendRequest.setDeleted(friendRequest.getId());

            //   sent Noti for sender friend request
            this.notificationRepository.save(Notification.builder()
                    .senderType(Notification.SENDER_TYPE.SYSTEM)
                    .receiverId(sender.getId())
                    .receiverType(Notification.RECEIVER_TYPE.USER)
                    .referenceId(receiver.getId())
                    .referenceType(Notification.REFERENCE_TYPE.USER)
                    .content(MessageBundle.getMessage("message.notification.accept.friend.sender", sender.getFullName()))
                    .status(Notification.STATUS.NOT_SEEN)
                    .createdBy(0L)
                    .build());

            //   sent Noti for receiver friend request
            this.notificationRepository.save(Notification.builder()
                    .senderType(Notification.SENDER_TYPE.SYSTEM)
                    .receiverId(receiver.getId())
                    .receiverType(Notification.RECEIVER_TYPE.USER)
                    .referenceId(sender.getId())
                    .referenceType(Notification.REFERENCE_TYPE.USER)
                    .content(MessageBundle.getMessage("message.notification.accept.friend.sender", receiver.getFullName()))
                    .status(Notification.STATUS.NOT_SEEN)
                    .createdBy(0L)
                    .build());

            return friendRequestRepository.save(friendRequest);
        }

        //Save FriendRequest
        return this.friendRequestRepository.save(FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequest.STATUS.PENDING)
                .build());
    }

    private List<User> validate(CurrentUser currentUser, Long receiverId) {
        Long senderId = currentUser.getId();

        // Kiem tra tai khoan cua 2 nguoi co ACTIVE khong
        List<User> twoUser = this.validateActiveUser(senderId, receiverId);

        User senderUser = twoUser.get(0);
        User receiverUser = twoUser.get(1);

        // Kiem tra co phai gui ket ban cho chinh minh khong
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.send.friend.request.yourself"));
        }

        // Kiem tra 2 nguoi da la ban hay chua
        if (this.friendRepository.areFriends(senderId, receiverId)) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.friend.already"));
        }

        // Kiem tra 2 nguoi co block nhau khong
        if (this.blockRepository.areBlockMutualFriends(senderId, receiverId)) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.friend.block"));
        }

        // Kiem tra gioi han ban be cua 2 nguoi co thoa man khong
        if (this.friendRepository.countFriends(senderId) >= FriendConstant.MAX_FRIEND_NUM) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.friend.reach.limit.sender", FriendConstant.MAX_FRIEND_NUM));
        }

        if (this.friendRepository.countFriends(receiverId) >= FriendConstant.MAX_FRIEND_NUM) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.friend.reach.limit.receive", FriendConstant.MAX_FRIEND_NUM));
        }

        //  Kiem tra da ton tai friend-request chua
        if (this.friendRequestRepository.alreadySentFriendRequest(senderId, receiverId)) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.friend.request.sent.already"));
        }

        //  Kiem tra so luong friend-request cua it nhat 1 trong 2 nguoi co dat gioi han khong
        if (this.friendRequestRepository.countFriendRequestSent(senderId) >= FriendConstant.MAX_FRIEND_REQUEST_NUM) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.friend.reach.limit.request.sender", FriendConstant.MAX_FRIEND_REQUEST_NUM));
        }

        if (this.friendRequestRepository.countFriendRequestReceived(receiverId) > FriendConstant.MAX_FRIEND_REQUEST_NUM) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.friend.reach.limit.request.receiver", FriendConstant.MAX_FRIEND_REQUEST_NUM));
        }

        return List.of(senderUser, receiverUser);
    }


    private List<User> validateActiveUser(Long senderId, Long receiverId) {
        User senderUser = this.userService.getUserActive(senderId);
        if (senderUser == null) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.friend.not.exist"));
        }

        User receiverUser = this.userService.getUserActive(receiverId);
        if (receiverUser == null) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.friend.not.exist"));
        }

        return List.of(senderUser, receiverUser);
    }
}
