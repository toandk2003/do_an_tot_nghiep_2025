package org.yenln8.ChatApp.services.interfaces;

import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.ChangeStatusNotificationRequestDto;
import org.yenln8.ChatApp.dto.request.GetNotificationRequestDto;

public interface NotificationService {
    BaseResponseDto getNotification(GetNotificationRequestDto form);
    BaseResponseDto changeStatusNotification(Long notificationId, ChangeStatusNotificationRequestDto form);

}
