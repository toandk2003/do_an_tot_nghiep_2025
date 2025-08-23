package org.yenln8.ChatApp.services.serviceImpl.user.implement;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.response.BlockResponseDto;
import org.yenln8.ChatApp.dto.response.UnBlockResponseDto;
import org.yenln8.ChatApp.entity.Block;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.BlockRepository;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.BlockService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.UnBlockService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UnBlockServiceImpl implements UnBlockService {
    private UserRepository userRepository;
    private BlockRepository blockRepository;
    private GetFullInfoAboutUserService  getFullInfoAboutUserService;

    @Override
    public BaseResponseDto call(Long blockId) {
        Block block = this.blockRepository.findByIdAndDeletedAtIsNull(blockId);

        if (block == null) {
            throw new IllegalArgumentException("Block does not exist");
        }

        CurrentUser currentUser = ContextService.getCurrentUser();
        User user = userRepository.findByIdAndStatus(currentUser.getId(), User.STATUS.ACTIVE).orElseThrow(() -> new IllegalArgumentException("User does not exist"));

        if(!user.getId().equals(block.getUser().getId())) {
           throw  new IllegalArgumentException("You must not unblock this user");
        }

        //update
        block.setDeleted(blockId);
        block.setDeletedAt(LocalDateTime.now());

        this.blockRepository.save(block);

        User blockedUser = block.getBlockedUser();

        UnBlockResponseDto responseDto = UnBlockResponseDto.builder()
                .id(blockId)
                .user(this.getFullInfoAboutUserService.call(user))
                .blockedUser(this.getFullInfoAboutUserService.call(blockedUser))
                .build();

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(200)
                .data(responseDto)
                .message("UnBlock success successfully.")
                .build();
    }
}
