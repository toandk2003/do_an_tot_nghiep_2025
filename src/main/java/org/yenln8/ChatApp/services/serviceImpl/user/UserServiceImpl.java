package org.yenln8.ChatApp.services.serviceImpl.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.ExploreRequestDto;
import org.yenln8.ChatApp.services.interfaces.UserService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.ExploreService;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private ExploreService exploreService;

    @Override
    public BaseResponseDto explore(ExploreRequestDto form) {
        return this.exploreService.call(form);
    }
}
