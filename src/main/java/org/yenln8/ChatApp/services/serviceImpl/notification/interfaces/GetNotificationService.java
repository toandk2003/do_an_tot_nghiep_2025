package org.yenln8.ChatApp.services.serviceImpl.notification.interfaces;

import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.GetNotificationRequestDto;

public interface GetNotificationService {
    BaseResponseDto call(GetNotificationRequestDto form);

}
