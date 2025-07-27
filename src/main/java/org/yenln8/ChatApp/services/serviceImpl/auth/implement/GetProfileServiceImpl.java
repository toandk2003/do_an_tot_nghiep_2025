package org.yenln8.ChatApp.services.serviceImpl.auth.implement;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.LearningLanguage;
import org.yenln8.ChatApp.entity.NativeLanguage;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.GetProfileService;

@Slf4j
@AllArgsConstructor
@Service
public class GetProfileServiceImpl implements GetProfileService {
    private UserRepository userRepository;

    @Override
    public BaseResponseDto call(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser currentUser) {
            Long userId = currentUser.getId();

            User user = userRepository.findByUserIdWithProfileAndNativeAndLearning(userId).orElse(null);

            if(user == null) {
                throw new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "User", "id", userId));
            }
            NativeLanguage nativeLanguage = user.getProfile().getNativeLanguage();
            LearningLanguage learningLanguage = user.getProfile().getLearningLanguage();

            GetProfileResponseDto response = GetProfileResponseDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .location(user.getProfile().getLocation())
                    .bio(user.getProfile().getBio())
                    .isOnboarded(user.getStatus().equals(User.STATUS.NO_ONBOARDING) ? Boolean.FALSE : Boolean.TRUE)
                    .nativeLanguage(NativeLanguage.builder().id(nativeLanguage.getId()).name(nativeLanguage.getName()).build())
                    .learningLanguage(LearningLanguage.builder().id(learningLanguage.getId()).name(learningLanguage.getName()).build())
                    .build();

            return BaseResponseDto.builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message(MessageBundle.getMessage("message.get.profile.success"))
                    .data(response)
                    .build();
        }
        return null;
    }
}
