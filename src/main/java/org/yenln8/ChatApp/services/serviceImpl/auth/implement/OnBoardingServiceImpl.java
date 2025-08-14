package org.yenln8.ChatApp.services.serviceImpl.auth.implement;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.request.OnBoardingRequestDto;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.repository.*;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.OnBoardingService;

@Slf4j
@AllArgsConstructor
@Service
public class OnBoardingServiceImpl implements OnBoardingService {
    private ProfileRepository profileRepository;
    private NativeLanguageRepository nativeLanguageRepository;
    private LearningLanguageRepository learningLanguageRepository;
    private UserRepository userRepository;
    private AttachmentRepository attachmentRepository;
    private LimitResourceRepository limitResourceRepository;

    @Override
    public BaseResponseDto call(OnBoardingRequestDto form, HttpServletRequest request) {
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
        this.attachmentRepository.save(attachment);

        // insert user
        user.setProfile(profile);
        user.setStatus(User.STATUS.ACTIVE);
        this.userRepository.save(user);

        // insert limit resource for new user
        LimitResource limitResource = LimitResource.builder()
                .maxLimit(S3Constant.MAX_LIMIT_RESOURCE)
                .type(LimitResource.TYPE.MEDIA)
                .currentUsage(0)
                .status(LimitResource.STATUS.FREE)
                .userId(userId)
                .build();
        this.limitResourceRepository.save(limitResource);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Great, Onboard successfully, welcome to ChatApp")
                .build();
    }
}
