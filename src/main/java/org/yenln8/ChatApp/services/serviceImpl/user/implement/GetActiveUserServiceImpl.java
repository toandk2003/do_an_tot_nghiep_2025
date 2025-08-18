package org.yenln8.ChatApp.services.serviceImpl.user.implement;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.S3.DownloadFileResponseDto;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.base.PaginationResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.request.ExploreRequestDto;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.interfaces.S3Service;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.ExploreService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetActiveUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GetActiveUserServiceImpl implements GetActiveUserService {
    private UserRepository userRepository;

    @Override
    public User call(Long userId) {
       Optional<User> optionalUser = userRepository.findByIdAndStatus(userId,User.STATUS.ACTIVE);

        return optionalUser.orElse(null);
    }
}
