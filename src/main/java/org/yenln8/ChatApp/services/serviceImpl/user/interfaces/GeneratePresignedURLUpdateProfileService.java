package org.yenln8.ChatApp.services.serviceImpl.user.interfaces;

import org.yenln8.ChatApp.dto.S3.UploadFileRequestDto;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;

public interface GeneratePresignedURLUpdateProfileService {
    BaseResponseDto call(UploadFileRequestDto file);
}
