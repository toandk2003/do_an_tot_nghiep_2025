package org.yenln8.ChatApp.services.serviceImpl.auth.implement;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.common.util.RedisService;
import org.yenln8.ChatApp.dto.S3.DownloadFileResponseDto;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.request.LearningLanguageMiniDto;
import org.yenln8.ChatApp.dto.request.NativeLanguageMiniDto;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.interfaces.S3Service;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.GetProfileService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class GetProfileServiceImpl implements GetProfileService {
    private UserRepository userRepository;
    private S3Service s3Service;
    private RedisService redisService;

    @Override
    public BaseResponseDto call(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LearningLanguageLocale.LOCALE learningLocale = LocaleContextHolder.getLocale().getLanguage().equals("en") ? LearningLanguageLocale.LOCALE.ENGLISH : LearningLanguageLocale.LOCALE.VIETNAMESE;
        NativeLanguageLocale.LOCALE nativeLocale = LocaleContextHolder.getLocale().getLanguage().equals("en") ? NativeLanguageLocale.LOCALE.ENGLISH : NativeLanguageLocale.LOCALE.VIETNAMESE;

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

            NativeLanguageMiniDto nativeLanguage = Optional.ofNullable(profile)
                    .map(Profile::getNativeLanguage)
                    .map(x -> NativeLanguageMiniDto.builder()
                            .id(x.getId())
                            .name(x.getNativeLanguageLocales()
                                    .stream()
                                    .filter(item -> item.getLocale().equals(nativeLocale))
                                    .findFirst()
                                    .map(NativeLanguageLocale::getName)
                                    .get()
                            )
                            .build())
                    .orElse(null);

            LearningLanguageMiniDto learningLanguage = Optional.ofNullable(profile)
                    .map(Profile::getLearningLanguage)
                    .map(x -> LearningLanguageMiniDto.builder()
                            .id(x.getId())
                            .name(x.getLearningLanguageLocales()
                                    .stream()
                                    .filter(item -> item.getLocale().equals(learningLocale))
                                    .findFirst()
                                    .map(LearningLanguageLocale::getName)
                                    .get()
                            )
                            .build())
                    .orElse(null);


            Long attachmentId = Optional.ofNullable(profile).map(Profile::getAvatar).map(Attachment::getId).orElse(null);
            String fileNameInS3 = Optional.ofNullable(profile).map(Profile::getAvatar).map(Attachment::getFileNameInS3).orElse(null);

            if (fileNameInS3 == null) {
                GetProfileResponseDto response = GetProfileResponseDto.builder()
                        .id(id)
                        .email(email)
                        .fullName(fullName)
                        .isOnboarded(isOnboarded)
                        .location(location)
                        .bio(bio)
                        .nativeLanguage(nativeLanguage)
                        .learningLanguage(learningLanguage)
                        .rowVersion(user.getRowVersion())
                        .build();

                return BaseResponseDto.builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message(MessageBundle.getMessage("message.get.profile.success"))
                        .data(response)
                        .build();
            }

            DownloadFileResponseDto downloadFileResponse = this.s3Service.downloadFile(fileNameInS3, S3Constant.AVATAR_PRIVATE_BUCKET);

            String profilePic = downloadFileResponse.getDownloadUrl();

            Long lastOnlineSecondFromEpoch = this.redisService.getKey(this.redisService.getKeyLastOnlineWithPrefix(email), Long.class);

            LocalDateTime lastOnlineTime = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(lastOnlineSecondFromEpoch),
                    ZoneId.systemDefault());

            GetProfileResponseDto response = GetProfileResponseDto.builder()
                    .id(id)
                    .email(email)
                    .fullName(fullName)
                    .isOnboarded(isOnboarded)
                    .location(location)
                    .bio(bio)
                    .attachmentId(attachmentId)
                    .nativeLanguage(nativeLanguage)
                    .learningLanguage(learningLanguage)
                    .attachmentId(attachmentId)
                    .profilePic(profilePic)
                    .lastOnline(lastOnlineTime)
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
