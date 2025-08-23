package org.yenln8.ChatApp.services.serviceImpl.user.interfaces;

import org.yenln8.ChatApp.dto.base.BaseResponseDto;

public interface UnBlockService {
    BaseResponseDto call(Long blockId);
}
