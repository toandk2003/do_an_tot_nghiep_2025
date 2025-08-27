package org.yenln8.ChatApp.services.serviceImpl.friend.implement;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.base.PaginationResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.request.GetListFriendRequestDto;
import org.yenln8.ChatApp.dto.response.GetListFriendResponseDto;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.Friend;
import org.yenln8.ChatApp.entity.LearningLanguage;
import org.yenln8.ChatApp.entity.NativeLanguage;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.FriendRepository;
import org.yenln8.ChatApp.repository.LearningLanguageRepository;
import org.yenln8.ChatApp.repository.NativeLanguageRepository;
import org.yenln8.ChatApp.services.serviceImpl.friend.interfaces.GetListFriendService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class GetListFriendServiceImpl implements GetListFriendService {
    private FriendRepository friendRepository;
    private GetFullInfoAboutUserService getFullInfoAboutUserService;
    private LearningLanguageRepository learningLanguageRepository;
    private NativeLanguageRepository nativeLanguageRepository;

    @Override
    public BaseResponseDto call(GetListFriendRequestDto form) {
        CurrentUser currentUser = ContextService.getCurrentUser();
        Long userId = currentUser.getId();
        Long learningLanguageId = form.getLearningLanguageId();
        Long nativeLanguageId = form.getNativeLanguageId();
        String fullName = form.getFullName();

        int currentPage = form.getCurrentPage().intValue();
        int pageSize = form.getPageSize().intValue();
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize);

        LearningLanguage learningLanguage = learningLanguageId == null ? null : this.learningLanguageRepository.findById(learningLanguageId).orElse(null);
        NativeLanguage nativeLanguage = nativeLanguageId == null ? null : this.nativeLanguageRepository.findById(nativeLanguageId).orElse(null);

        LearningLanguage.CODE learningLanguageCode = Optional.ofNullable(learningLanguage).map(LearningLanguage::getCode).orElse(null);
        NativeLanguage.CODE nativeLanguageCode = Optional.ofNullable(nativeLanguage).map(NativeLanguage::getCode).orElse(null);

        List<LearningLanguage> learningLanguages = this.learningLanguageRepository.findAllByCode(learningLanguageCode);
        List<NativeLanguage> nativeLanguages = this.nativeLanguageRepository.findAllByCode(nativeLanguageCode);

        List<Long> learningLanguageIds = learningLanguages.isEmpty() ? null : learningLanguages.stream().map(LearningLanguage::getId).toList();
        List<Long> nativeLanguageIds = nativeLanguages.isEmpty() ? null : nativeLanguages.stream().map(NativeLanguage::getId).toList();

        Page<Friend> friendsPageable = this.friendRepository.getFriends(userId, learningLanguageIds, nativeLanguageIds, fullName, pageRequest);

        List<Friend> friends = friendsPageable.getContent();
        List<GetListFriendResponseDto> result = friends.stream().map(friend -> {
            User myFriend = friend.getUser1().getId().equals(userId) ? friend.getUser2() : friend.getUser1();

            GetProfileResponseDto myFriendFullInfo = this.getFullInfoAboutUserService.call(myFriend);

            return GetListFriendResponseDto.builder()
                    .id(friend.getId())
                    .user(myFriendFullInfo)
                    .build();
        }).toList();

        PaginationResponseDto<Friend, GetListFriendResponseDto> data = PaginationResponseDto.of(friendsPageable, result);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(200)
                .data(data)
                .message("Here are full info of your friends")
                .build();
    }
}
