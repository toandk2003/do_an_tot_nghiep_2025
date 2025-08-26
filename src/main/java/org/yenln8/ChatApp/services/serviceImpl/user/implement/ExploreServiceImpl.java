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
import org.yenln8.ChatApp.repository.*;
import org.yenln8.ChatApp.services.interfaces.S3Service;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.ExploreService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ExploreServiceImpl implements ExploreService {
    private UserRepository userRepository;
    private S3Service s3Service;
    private FriendRepository friendRepository;
    private FriendRequestRepository friendRequestRepository;
    private LearningLanguageRepository learningLanguageRepository;
    private NativeLanguageRepository nativeLanguageRepository;

    @Override
    public BaseResponseDto call(ExploreRequestDto form) {
        Authentication securityContextHolder = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser currentUser = (CurrentUser) securityContextHolder.getPrincipal();
        Long userId = currentUser.getId();
        Long learningLanguageId = form.getLearningLanguageId();
        Long nativeLanguageId = form.getNativeLanguageId();
//        User user = this.userRepository.findByUserIdWithProfileAndNativeAndLearning(userId).orElseThrow(() -> new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "User", "id", userId)));

        LearningLanguage learningLanguage = this.learningLanguageRepository.findById(learningLanguageId).orElseThrow(() -> new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "LearningLanguage", "id", "null")));
        NativeLanguage nativeLanguage = this.nativeLanguageRepository.findById(nativeLanguageId).orElseThrow(() -> new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "NativeLanguage", "id", "null")));

        LearningLanguage.CODE learningLanguageCode = learningLanguage.getCode();
        NativeLanguage.CODE nativeLanguageCode = nativeLanguage.getCode();

        long currentDay = LocalDateTime.now().toLocalDate().toEpochDay();
        Long currentPage = form.getCurrentPage();
        Long pageSize = form.getPageSize();

        User.STATUS userStatus = User.STATUS.ACTIVE;

        PageRequest pageRequest = PageRequest.of(currentPage.intValue(), pageSize.intValue());

        List<LearningLanguage> learningLanguages = this.learningLanguageRepository.findAllByCode(learningLanguageCode);
        List<NativeLanguage> nativeLanguages = this.nativeLanguageRepository.findAllByCode(nativeLanguageCode);

        //  retrieve other user not myself
        //  retrieve other user not friend
        //  retrieve other user not in list request friend sent
        //  retrieve other user not in list request friend receive

        List<Long> friendIds = this.friendRepository.getFriendIds(userId);
        List<Long> friendRequestSentIds = this.friendRequestRepository.getFriendRequestIdsSent(userId);
        List<Long> friendRequestReceivedIds = this.friendRequestRepository.getFriendRequestIdsReceived(userId);
        List<Long> learningLanguageIds = learningLanguages.stream().map(LearningLanguage::getId).toList();
        List<Long> nativeLanguageIds = nativeLanguages.stream().map(NativeLanguage::getId).toList();


        List<Long> avoidUserIds = new ArrayList<>();
        avoidUserIds.add(userId);
        avoidUserIds.addAll(friendIds);
        avoidUserIds.addAll(friendRequestSentIds);
        avoidUserIds.addAll(friendRequestReceivedIds);

        Page<User> userRecommends = this.userRepository.findByNativeLanguageIdAndLearningLanguageIdAndStatusAndIdNotInAndDeletedAtIsNull(
                nativeLanguageIds,
                learningLanguageIds,
                userStatus,
                currentDay,
                avoidUserIds,
                pageRequest);

        List<GetProfileResponseDto> usersAfterConvert = this.convert(userRecommends.getContent());
        PaginationResponseDto<User, GetProfileResponseDto> result = PaginationResponseDto.of(userRecommends, usersAfterConvert);

        return BaseResponseDto.builder()
                .success(Boolean.TRUE)
                .message("Success")
                .data(result)
                .build();
    }

    private List<GetProfileResponseDto> convert(List<User> userRecommends) {
        return userRecommends.stream().map((user) -> {
                    Long userId = user.getId();
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

                    return GetProfileResponseDto.builder()
                            .id(userId)
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
                }
        ).toList();
    }
}
