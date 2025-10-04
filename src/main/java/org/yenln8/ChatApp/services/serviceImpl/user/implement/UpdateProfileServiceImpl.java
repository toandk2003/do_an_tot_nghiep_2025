package org.yenln8.ChatApp.services.serviceImpl.user.implement;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.request.UpdateProfileRequestDto;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.event.synchronize.SynchronizeUpdateUserEvent;
import org.yenln8.ChatApp.repository.*;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.UpdateProfileService;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UpdateProfileServiceImpl implements UpdateProfileService {
    private ProfileRepository profileRepository;
    private NativeLanguageRepository nativeLanguageRepository;
    private LearningLanguageRepository learningLanguageRepository;
    private UserRepository userRepository;
    private AttachmentRepository attachmentRepository;
    private GetFullInfoAboutUserService getFullInfoAboutUserService;
    private EventRepository eventRepository;
    private ObjectMapper objectMapper;

    @Override
    public BaseResponseDto call(UpdateProfileRequestDto form) throws Exception {

        try {
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
            attachment.setOwnerId(profile.getId());
            attachment.setStatus(Attachment.STATUS.CONFIRMED);
            this.attachmentRepository.save(attachment);

            profile.setBio(form.getBio());
            profile.setLocation(form.getLocation());
            profile.setNativeLanguage(nativeLanguage);
            profile.setLearningLanguage(learningLanguage);
            profile.setAvatar(attachment);
            this.profileRepository.save(profile);

            //update attachment
            user.setProfile(profile);
            user.setRowVersion(user.getRowVersion() + 1);
            this.userRepository.save(user);

            GetProfileResponseDto userFullInfo = this.getFullInfoAboutUserService.call(user);

            // sync user
            SynchronizeUpdateUserEvent synchronizeUpdateUserEvent = SynchronizeUpdateUserEvent.builder()
                    .email(userFullInfo.getEmail())
                    .bio(userFullInfo.getBio())
                    .location(userFullInfo.getLocation())
                    .learningLanguageId(userFullInfo.getLearningLanguage().getId())
                    .nativeLanguageId(userFullInfo.getNativeLanguage().getId())
                    .avatar(userFullInfo.getFileNameInS3())
                    .bucket(S3Constant.AVATAR_PRIVATE_BUCKET)
                    .rowVersion(userFullInfo.getRowVersion())
                    .eventType(Event.TYPE.SYNC_UPDATE_USER)
                    .build();

            eventRepository.save(Event.builder()
                    .payload(objectMapper.writeValueAsString(synchronizeUpdateUserEvent))
                    .destination("sync-stream")
                    .status(Event.STATUS.WAIT_TO_SEND)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());

            return BaseResponseDto.builder()
                    .success(true)
                    .statusCode(HttpStatus.OK.value())
                    .message("Great, Update profile for user successfully.")
                    .data(userFullInfo)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
