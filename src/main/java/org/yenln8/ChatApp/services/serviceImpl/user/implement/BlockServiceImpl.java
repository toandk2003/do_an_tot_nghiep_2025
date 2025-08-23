package org.yenln8.ChatApp.services.serviceImpl.user.implement;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.response.BlockResponseDto;
import org.yenln8.ChatApp.entity.Block;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.BlockRepository;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.BlockService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetActiveUserService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BlockServiceImpl implements BlockService {
    private UserRepository userRepository;
    private BlockRepository blockRepository;
    private GetFullInfoAboutUserService  getFullInfoAboutUserService;

    @Override
    public BaseResponseDto call(Long userId) {
        Optional<User> blockedUser = userRepository.findByIdAndStatus(userId, User.STATUS.ACTIVE);

        if (blockedUser.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        CurrentUser currentUser = ContextService.getCurrentUser();

        if(userId.equals(currentUser.getId())) {
            throw new IllegalArgumentException("You can not block yourself");

        }
        User user = userRepository.findByIdAndStatus(currentUser.getId(), User.STATUS.ACTIVE).orElse(null);


        Block block = this.blockRepository.findBlock(currentUser.getId(), userId);

        if (block != null) {
            throw new IllegalArgumentException("Block already exists");
        }

        Block blockToSave = this.blockRepository.save(Block.builder()
                .user(user)
                .blockedUser(blockedUser.get())
                .build());

        BlockResponseDto responseDto = BlockResponseDto.builder()
                .id(blockToSave.getId())
                .user(this.getFullInfoAboutUserService.call(user))
                .blockedUser(this.getFullInfoAboutUserService.call(blockedUser.get()))
                .build();

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(200)
                .data(responseDto)
                .message("Block success successfully.")
                .build();
    }
}
