package org.yenln8.ChatApp.services.serviceImpl.user.implement;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.common.util.RedisService;
import org.yenln8.ChatApp.dto.S3.DownloadFileResponseDto;
import org.yenln8.ChatApp.dto.request.LearningLanguageMiniDto;
import org.yenln8.ChatApp.dto.request.NativeLanguageMiniDto;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.services.interfaces.S3Service;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class GetFullInfoAboutUserServiceImpl implements GetFullInfoAboutUserService {
    private S3Service s3Service;
    private RedisService redisService;

    @Override
    public GetProfileResponseDto call(User user) {

        LearningLanguageLocale.LOCALE learningLocale = LocaleContextHolder.getLocale().getLanguage().equals("en") ? LearningLanguageLocale.LOCALE.ENGLISH : LearningLanguageLocale.LOCALE.VIETNAMESE  ;
        NativeLanguageLocale.LOCALE nativeLocale = LocaleContextHolder.getLocale().getLanguage().equals("en") ? NativeLanguageLocale.LOCALE.ENGLISH : NativeLanguageLocale.LOCALE.VIETNAMESE  ;

        Long userId = user.getId();
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
            return GetProfileResponseDto.builder()
                    .id(userId)
                    .email(email)
                    .fullName(fullName)
                    .isOnboarded(isOnboarded)
                    .location(location)
                    .bio(bio)
                    .nativeLanguage(nativeLanguage)
                    .learningLanguage(learningLanguage)
                    .rowVersion(user.getRowVersion())
                    .build();
        }

        DownloadFileResponseDto downloadFileResponse = this.s3Service.downloadFile(fileNameInS3, S3Constant.AVATAR_PRIVATE_BUCKET);

        String profilePic = downloadFileResponse.getDownloadUrl();

        Long lastOnlineSecondFromEpoch = this.redisService.getKey(this.redisService.getKeyLastOnlineWithPrefix(email), Long.class);

        LocalDateTime lastOnlineTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(lastOnlineSecondFromEpoch),
                ZoneId.systemDefault()
        );
        return GetProfileResponseDto.builder()
                .id(userId)
                .email(email)
                .fullName(fullName)
                .isOnboarded(isOnboarded)
                .location(location)
                .bio(bio)
                .nativeLanguage(nativeLanguage)
                .learningLanguage(learningLanguage)
                .attachmentId(attachmentId)
                .fileNameInS3(fileNameInS3)
                .profilePic(profilePic)
                .lastOnline(lastOnlineTime)
                .rowVersion(user.getRowVersion())
                .build();
    }
}
