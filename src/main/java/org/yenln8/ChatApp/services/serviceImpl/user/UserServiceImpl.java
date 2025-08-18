package org.yenln8.ChatApp.services.serviceImpl.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.ExploreRequestDto;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.services.interfaces.UserService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.ExploreService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetActiveUserService;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private ExploreService exploreService;
    private GetActiveUserService getActiveUserService;

    @Override
    public BaseResponseDto explore(ExploreRequestDto form) {
        return this.exploreService.call(form);
    }

    @Override
    public BaseResponseDto block(ExploreRequestDto form) {
        return null;
    }

    @Override
    public BaseResponseDto unblock(ExploreRequestDto form) {
        return null;
    }

    @Override
    public User getUserActive(Long userId)  {
        return this.getActiveUserService.call(userId);
    }
}
