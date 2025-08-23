package org.yenln8.ChatApp.services.serviceImpl.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.ExploreRequestDto;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.services.interfaces.UserService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.BlockService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.ExploreService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetActiveUserService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.UnBlockService;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private ExploreService exploreService;
    private GetActiveUserService getActiveUserService;
    private BlockService blockService;
    private UnBlockService unBlockService;

    @Override
    public BaseResponseDto explore(ExploreRequestDto form) {
        return this.exploreService.call(form);
    }

    @Override
    @Transactional
    public BaseResponseDto block(Long userId) {
        return blockService.call(userId);
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
}
