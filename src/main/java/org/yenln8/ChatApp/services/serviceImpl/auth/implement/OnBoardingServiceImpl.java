package org.yenln8.ChatApp.services.serviceImpl.auth.implement;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.request.OnBoardingRequestDto;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.event.synchronize.SynchronizeUserEvent;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.repository.*;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.OnBoardingService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;

import java.time.LocalDateTime;

@Slf4j
@NoArgsConstructor
@Service
public class OnBoardingServiceImpl implements OnBoardingService {
    @Value("${app.redis.streams.sync-stream}")
    private String syncStream;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private NativeLanguageRepository nativeLanguageRepository;

    @Autowired
    private LearningLanguageRepository learningLanguageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private GetFullInfoAboutUserService getFullInfoAboutUserService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @Override
    public BaseResponseDto call(OnBoardingRequestDto form, HttpServletRequest request) throws Exception {
        Authentication securityContextHolder = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser currentUser = (CurrentUser) securityContextHolder.getPrincipal();
        Long userId = currentUser.getId();
        Long attachmentId = form.getAttachmentId();

        User user = this.userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "User", "id", userId)));

        if (user.getProfile() != null) {
            throw new IllegalArgumentException(MessageBundle.getMessage("error.object.is.existed", "Profile", "id", user.getProfile().getId()));
        }

        NativeLanguage nativeLanguage = this.nativeLanguageRepository.findById(form.getNativeLanguageId()).orElseThrow(() -> new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "NativeLanguage", "id", form.getNativeLanguageId())));
        LearningLanguage learningLanguage = this.learningLanguageRepository.findById(form.getLearningLanguageId()).orElseThrow(() -> new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "LearningLanguage", "id", form.getLearningLanguageId())));

        Attachment attachment = this.attachmentRepository.findById(attachmentId).orElseThrow(() -> new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "Attachment", "id", attachmentId)));

        Profile profile = Profile.builder()
                .bio(form.getBio())
                .location(form.getLocation())
                .nativeLanguage(nativeLanguage)
                .learningLanguage(learningLanguage)
                .avatar(attachment)
                .build();

        // insert profile
        this.profileRepository.save(profile);

        //update attachment
        attachment.setOwnerId(profile.getId());
        attachment.setStatus(Attachment.STATUS.CONFIRMED);
        this.attachmentRepository.save(attachment);

        // update user
        user.setProfile(profile);
        user.setStatus(User.STATUS.ACTIVE);
        this.userRepository.save(user);

        GetProfileResponseDto userFullInfo = getFullInfoAboutUserService.call(user);

        // sync user
        SynchronizeUserEvent synchronizeUserEvent = SynchronizeUserEvent.builder()
                .userId(userFullInfo.getId())
                .email(userFullInfo.getEmail())
                .fullName(userFullInfo.getFullName())
                .bio(userFullInfo.getBio())
                .location(userFullInfo.getLocation())
                .learningLanguageId(userFullInfo.getLearningLanguage().getId())
                .nativeLanguageId(userFullInfo.getNativeLanguage().getId())
                .nativeLanguageName(userFullInfo.getNativeLanguage().getName())
                .learningLanguageName(userFullInfo.getLearningLanguage().getName())
                .avatar(userFullInfo.getFileNameInS3())
                .bucket(S3Constant.AVATAR_PRIVATE_BUCKET)
                .status(User.STATUS.ACTIVE.toString())
                .role(User.ROLE.USER.toString())
                .maxLimitResourceMedia(S3Constant.MAX_LIMIT_RESOURCE)
                .currentUsageResourceMedia(0L)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .rowVersion(userFullInfo.getRowVersion())
                .deleted(0)
                .eventType(Event.TYPE.SYNC_USER)
                .build();

        eventRepository.save(Event.builder()
                .payload(objectMapper.writeValueAsString(synchronizeUserEvent))
                .destination(syncStream)
                .status(Event.STATUS.WAIT_TO_SEND)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Great, Onboard successfully, welcome to ChatApp")
                .data(userFullInfo)
                .build();
    }
}
