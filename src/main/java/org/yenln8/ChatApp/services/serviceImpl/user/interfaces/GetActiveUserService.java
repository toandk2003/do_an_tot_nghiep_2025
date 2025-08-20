package org.yenln8.ChatApp.services.serviceImpl.user.interfaces;

import org.yenln8.ChatApp.entity.User;

public interface GetActiveUserService {
    User call(Long userId);
}
