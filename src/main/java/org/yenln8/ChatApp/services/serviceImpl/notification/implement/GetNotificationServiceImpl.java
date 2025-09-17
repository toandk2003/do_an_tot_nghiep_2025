package org.yenln8.ChatApp.services.serviceImpl.notification.implement;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.base.PaginationResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.request.GetNotificationRequestDto;
import org.yenln8.ChatApp.dto.response.GetNotificationResponseDto;
import org.yenln8.ChatApp.dto.response.NotificationConvertedFromEntityDto;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.repository.NotificationRepository;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.serviceImpl.notification.interfaces.GetNotificationService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;

import java.util.List;

@Service
@AllArgsConstructor
public class GetNotificationServiceImpl implements GetNotificationService {
    private NotificationRepository notificationRepository;
    private UserRepository userRepository;
    private GetFullInfoAboutUserService getFullInfoAboutUserService;

    @Override
    public BaseResponseDto call(GetNotificationRequestDto form) {
        CurrentUser currentUser = ContextService.getCurrentUser();
        Long userId = currentUser.getId();

        int currentPage = form.getCurrentPage().intValue();
        int pageSize = form.getPageSize().intValue();
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize);

        long countNotSeen = notificationRepository.countByReceiverIdAndStatus(userId, Notification.STATUS.NOT_SEEN);
        Page<Notification> notificationPageable = notificationRepository.findByReceiverIdAndStatusNotOrderByCreatedAtDesc(userId, Notification.STATUS.DELETED, pageRequest);

        List<Notification> notifications = notificationPageable.getContent();

        List<NotificationConvertedFromEntityDto> result = notifications.stream().map(notification -> {
            Long receiverId = notification.getReferenceId();

            User userReference = this.userRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("User not found for get Notification"));

            GetProfileResponseDto userReferenceFullInfo = this.getFullInfoAboutUserService.call(userReference);

            return NotificationConvertedFromEntityDto.builder()
                    .id(notification.getId())
                    .content(notification.getContent())
                    .status(notification.getStatus())
                    .type(notification.getType())
                    .createdAt(notification.getCreatedAt())
                    .seenAt(notification.getSeenAt())
                    .userReference(userReferenceFullInfo)
                    .build();
        }).toList();

        PaginationResponseDto<Notification, NotificationConvertedFromEntityDto> paginationResponseDto = PaginationResponseDto.of(notificationPageable, result);

        GetNotificationResponseDto data = GetNotificationResponseDto.builder()
                .notSeenNotificationNums(countNotSeen)
                .paginationInfo(paginationResponseDto)
                .build();

        return BaseResponseDto.builder()
                .success(true)
                .message("success")
                .statusCode(200)
                .data(data)
                .build();
    }
}
