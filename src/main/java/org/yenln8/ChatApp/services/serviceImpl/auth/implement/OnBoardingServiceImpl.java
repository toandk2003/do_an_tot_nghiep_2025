package org.yenln8.ChatApp.services.serviceImpl.auth.implement;

import io.swagger.v3.core.util.Json;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.ApiClient;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.common.util.Network;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.request.OnBoardingRequestDto;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.dto.synchronize.SynchronizeUserDto;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.repository.*;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.OnBoardingService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Service
public class OnBoardingServiceImpl implements OnBoardingService {
    @Value("${url.synchronize.chat-service}")
    private String chatServiceUrl;
    private ProfileRepository profileRepository;
    private NativeLanguageRepository nativeLanguageRepository;
    private LearningLanguageRepository learningLanguageRepository;
    private UserRepository userRepository;
    private AttachmentRepository attachmentRepository;
    private GetFullInfoAboutUserService getFullInfoAboutUserService;
    private ApiClient apiClient;

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
        attachment.setStatus(Attachment.STATUS.CONFIRMED);
        this.attachmentRepository.save(attachment);

        // update user
        user.setProfile(profile);
        user.setStatus(User.STATUS.ACTIVE);
        this.userRepository.save(user);

        GetProfileResponseDto userFullInfo = this.getFullInfoAboutUserService.call(user);
        SynchronizeUserDto synchronizeUserDto = SynchronizeUserDto.builder()
                .id("122223333333L")
                .build();

        this.apiClient.callPostExternalApi(chatServiceUrl + "/users", Json.pretty(synchronizeUserDto), Network.getTokenFromRequest(request));
        return BaseResponseDto.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Great, Onboard successfully, welcome to ChatApp")
                .data(userFullInfo)
                .build();
    }
}
