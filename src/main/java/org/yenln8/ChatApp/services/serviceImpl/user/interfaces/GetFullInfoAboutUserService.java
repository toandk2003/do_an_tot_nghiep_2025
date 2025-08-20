package org.yenln8.ChatApp.services.serviceImpl.user.interfaces;

import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.User;

public interface GetFullInfoAboutUserService {
    GetProfileResponseDto call(User user);
}
