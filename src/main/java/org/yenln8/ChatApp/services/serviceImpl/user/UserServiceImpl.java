package org.yenln8.ChatApp.services.serviceImpl.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yenln8.ChatApp.dto.S3.UploadFileRequestDto;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.ExploreRequestDto;
import org.yenln8.ChatApp.dto.request.UpdateProfileRequestDto;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.interfaces.UserService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.*;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private ExploreService exploreService;
    private GetActiveUserService getActiveUserService;
    private BlockService blockService;
    private UnBlockService unBlockService;
    private UpdateProfileService updateProfileService;
    private GeneratePresignedURLUpdateProfileService generatePresignedURLUpdateProfileService;
    private UserRepository userRepository;
    private GetFullInfoAboutUserService getFullInfoAboutUserService;

    @Override
    public BaseResponseDto explore(ExploreRequestDto form) {
        return this.exploreService.call(form);
    }

    @Override
    @Transactional
    public BaseResponseDto block(Long userId) {
        return blockService.call(userId);
    }

    public BaseResponseDto getDetail(String email) {
        log.info("email: {}", email);
        User user = userRepository.findByEmailAndDeletedAtIsNull(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

        return BaseResponseDto.builder()
                .success(true)
                .message("success")
                .statusCode(200)
                .data(getFullInfoAboutUserService.call(user))
                .build();

    }

    @Override
    @Transactional
    public BaseResponseDto unblock(Long blockId) {
        return this.unBlockService.call(blockId);
    }

    @Override
    public User getUserActive(Long userId) {
        return this.getActiveUserService.call(userId);
    }

    @Transactional
    @Override
    public BaseResponseDto updateProfile(UpdateProfileRequestDto form) throws Exception {
        return this.updateProfileService.call(form);
    }

    @Transactional
    @Override
    public BaseResponseDto generatePresignedURLUpdateProfile(UploadFileRequestDto file) {
        return this.generatePresignedURLUpdateProfileService.call(file);
    }
}
