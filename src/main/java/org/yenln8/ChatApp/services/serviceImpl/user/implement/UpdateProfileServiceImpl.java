package org.yenln8.ChatApp.services.serviceImpl.user.implement;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.request.UpdateProfileRequestDto;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.repository.*;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.UpdateProfileService;

@Service
@AllArgsConstructor
public class UpdateProfileServiceImpl implements UpdateProfileService {
    private ProfileRepository profileRepository;
    private NativeLanguageRepository nativeLanguageRepository;
    private LearningLanguageRepository learningLanguageRepository;
    private UserRepository userRepository;
    private AttachmentRepository attachmentRepository;

    @Override
    public BaseResponseDto call(UpdateProfileRequestDto form) {

        Authentication securityContextHolder = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser currentUser = (CurrentUser) securityContextHolder.getPrincipal();

        Long userId = currentUser.getId();
        Long attachmentId = form.getAttachmentId();

        User user = this.userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "User", "id", userId)));
        Profile profile = user.getProfile();
        if (profile == null) {
            throw new IllegalArgumentException("Profile is null");
        }

        NativeLanguage nativeLanguage = this.nativeLanguageRepository.findById(form.getNativeLanguageId()).orElseThrow(() -> new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "NativeLanguage", "id", form.getNativeLanguageId())));
        LearningLanguage learningLanguage = this.learningLanguageRepository.findById(form.getLearningLanguageId()).orElseThrow(() -> new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "LearningLanguage", "id", form.getLearningLanguageId())));

        Attachment attachment = this.attachmentRepository.findById(attachmentId).orElseThrow(() -> new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "Attachment", "id", attachmentId)));

        profile = profile.toBuilder()
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

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Great, Update profile for user successfully.")
                .build();
    }
}
