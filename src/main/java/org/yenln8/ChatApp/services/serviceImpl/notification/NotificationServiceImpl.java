package org.yenln8.ChatApp.services.serviceImpl.notification;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.GetNotificationRequestDto;
import org.yenln8.ChatApp.services.interfaces.NotificationService;
import org.yenln8.ChatApp.services.serviceImpl.notification.interfaces.GetNotificationService;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private GetNotificationService getNotificationService;

    @Override
    public BaseResponseDto getNotification(GetNotificationRequestDto form) {
        return this.getNotificationService.call(form);
    }
}
