package org.yenln8.ChatApp.services.serviceImpl.notification.implement;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.base.PaginationResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.request.ChangeStatusNotificationRequestDto;
import org.yenln8.ChatApp.dto.request.GetNotificationRequestDto;
import org.yenln8.ChatApp.dto.response.GetNotificationResponseDto;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.dto.response.NotificationConvertedFromEntityDto;
import org.yenln8.ChatApp.entity.Notification;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.NotificationRepository;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.serviceImpl.notification.interfaces.ChangeStatusNotificationService;
import org.yenln8.ChatApp.services.serviceImpl.notification.interfaces.GetNotificationService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ChangeStatusNotificationServiceImpl implements ChangeStatusNotificationService {
    private NotificationRepository notificationRepository;
    private UserRepository userRepository;
    private GetFullInfoAboutUserService getFullInfoAboutUserService;

    @Override
    public BaseResponseDto call(Long notificationId, ChangeStatusNotificationRequestDto form) {
        CurrentUser currentUser = ContextService.getCurrentUser();
        Long userId = currentUser.getId();
        Long statusId = form.getStatus();

        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new IllegalArgumentException("Notification id not found"));

        if (!notification.getReceiverId().equals(userId)) {
            throw new IllegalArgumentException("You are not allowed to change the status of this notification");
        }
        User user = this.userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(statusId >= 0 && statusId <= 2)) throw new IllegalArgumentException("Status must be between 0 and 2");

        Notification.STATUS status = notification.getStatus();

        if (statusId.equals(0L)) {
            status = Notification.STATUS.NOT_SEEN;
        } else if (statusId.equals(1L)) {
            status = Notification.STATUS.SEEN;
        } else if (statusId.equals(2L)) {
            status = Notification.STATUS.DELETED;
        }

        notification.setStatus(status);

        if (status.equals(Notification.STATUS.SEEN)) {
            notification.setSeenAt(LocalDateTime.now());
        }

        notificationRepository.save(notification);

        GetProfileResponseDto referenceUserFullInfo = this.getFullInfoAboutUserService.call(user);
        NotificationConvertedFromEntityDto response = NotificationConvertedFromEntityDto.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .status(notification.getStatus())
                .userReference(referenceUserFullInfo)
                .createdAt(notification.getCreatedAt())
                .seenAt(notification.getSeenAt())
                .build();

        return BaseResponseDto.builder()
                .success(true)
                .message("success")
                .statusCode(200)
                .data(response)
                .build();
    }
}
