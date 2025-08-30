package org.yenln8.ChatApp.services.serviceImpl.notification;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.ChangeStatusNotificationRequestDto;
import org.yenln8.ChatApp.dto.request.GetNotificationRequestDto;
import org.yenln8.ChatApp.services.interfaces.NotificationService;
import org.yenln8.ChatApp.services.serviceImpl.notification.interfaces.ChangeStatusNotificationService;
import org.yenln8.ChatApp.services.serviceImpl.notification.interfaces.GetNotificationService;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private GetNotificationService getNotificationService;
    private ChangeStatusNotificationService changeStatusNotificationService;

    @Override
    public BaseResponseDto getNotification(GetNotificationRequestDto form) {
        return this.getNotificationService.call(form);
    }

    @Override
    @Transactional
    public BaseResponseDto changeStatusNotification(Long notificationId, ChangeStatusNotificationRequestDto form) {
        return this.changeStatusNotificationService.call(notificationId,form);
    }
}
