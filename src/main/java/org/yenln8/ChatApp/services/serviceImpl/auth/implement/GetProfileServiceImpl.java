package org.yenln8.ChatApp.services.serviceImpl.auth.implement;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.S3.DownloadFileResponseDto;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.interfaces.S3Service;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.GetProfileService;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class GetProfileServiceImpl implements GetProfileService {
    private UserRepository userRepository;
    private S3Service s3Service;

    @Override
    public BaseResponseDto call(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser currentUser) {
            Long userId = currentUser.getId();

            User user = userRepository.findByUserIdWithProfileAndNativeAndLearning(userId).orElse(null);

            if (user == null) {
                throw new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "User", "id", userId));
            }

            Long id = user.getId();
            String email = user.getEmail().trim();
            String fullName = user.getFullName().trim();
            Boolean isOnboarded = user.getStatus().equals(User.STATUS.NO_ONBOARDING) ? Boolean.FALSE : Boolean.TRUE;

            Profile profile = user.getProfile();

            String location = Optional.ofNullable(profile).map(Profile::getLocation).orElse(null);
            String bio = Optional.ofNullable(profile).map(Profile::getBio).orElse(null);

            NativeLanguage nativeLanguage = Optional.ofNullable(profile)
                    .map(Profile::getNativeLanguage)
                    .map(x -> NativeLanguage.builder()
                            .id(x.getId())
                            .name(x.getName())
                            .build())
                    .orElse(null);

            LearningLanguage learningLanguage = Optional.ofNullable(profile).
                    map(Profile::getLearningLanguage)
                    .map(x -> LearningLanguage.builder()
                            .id(x.getId())
                            .name(x.getName())
                            .build())
                    .orElse(null);


            String fileNameInS3 = Optional.ofNullable(profile).map(Profile::getAvatar).map(Attachment::getFileNameInS3).orElse(null);

            DownloadFileResponseDto downloadFileResponse = this.s3Service.downloadFile(fileNameInS3, S3Constant.AVATAR_PRIVATE_BUCKET);

            String profilePic = downloadFileResponse.getDownloadUrl();

            GetProfileResponseDto response = GetProfileResponseDto.builder()
                    .id(id)
                    .email(email)
                    .fullName(fullName)
                    .isOnboarded(isOnboarded)
                    .location(location)
                    .bio(bio)
                    .nativeLanguage(nativeLanguage)
                    .learningLanguage(learningLanguage)
                    .profilePic(profilePic)
                    .rowVersion(user.getRowVersion())
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
