package org.yenln8.ChatApp.services.serviceImpl.friend.implement;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.constant.FriendConstant;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.BlockRepository;
import org.yenln8.ChatApp.repository.FriendRepository;
import org.yenln8.ChatApp.services.interfaces.UserService;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.MakeFriendService;

import java.util.List;

@Service
@AllArgsConstructor
public class MakeFriendServiceImpl implements MakeFriendService {

    private UserService userService;
    private FriendRepository friendRepository;
    private BlockRepository blockRepository;


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
        this.validate(currentUser, receiverId);
//        this.save(form,currentUser);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(200)
                .message("Success")//Bundle
                .build();
    }

    private void validate(CurrentUser currentUser, Long receiverId) {
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
        if(this.friendRepository.areFriends(senderId,receiverId)){
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.friend.already"));
        }

        // Kiem tra 2 nguoi co block nhau khong
        if(this.blockRepository.areBlockMutualFriends(senderId,receiverId)){
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.friend.block"));
        }
        // Kiem tra 2 nguoi co vo tinh gui ket ban cho nhau khong, neu co thi dong y luon
        //TODO

        // Kiem tra gioi han ban be cua 2 nguoi co thoa man khong
        if(this.friendRepository.countFriends(senderId) >= FriendConstant.MAX_FRIEND_NUM){
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.friend.reach.limit.sender",  FriendConstant.MAX_FRIEND_NUM));
        }

        if(this.friendRepository.countFriends(receiverId) >= FriendConstant.MAX_FRIEND_NUM){
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.friend.reach.limit.receive",  FriendConstant.MAX_FRIEND_NUM));
        }

        // Kiem tra da ton tai friend-request chua
        // Kiem tra so luong friend-request cua it nhat 1 trong 2 nguoi co dat gioi han khong
    }


    private List<User> validateActiveUser(Long senderId, Long receiverId) {
        User senderUser = this.userService.getUserActive(senderId);
        if (senderUser == null) {
            throw new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "User", "id", senderId));
        }

        User receiverUser = this.userService.getUserActive(receiverId);
        if (receiverUser == null) {
            throw new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "User", "id", receiverId));
        }

        return List.of(senderUser, receiverUser);
    }
}
