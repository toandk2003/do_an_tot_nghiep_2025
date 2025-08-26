package org.yenln8.ChatApp.services.interfaces;

import org.yenln8.ChatApp.dto.S3.UploadFileRequestDto;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.ExploreRequestDto;
import org.yenln8.ChatApp.dto.request.UpdateProfileRequestDto;
import org.yenln8.ChatApp.entity.User;

public interface UserService {
    BaseResponseDto explore(ExploreRequestDto form);
    BaseResponseDto block(Long userId);
    BaseResponseDto unblock(Long blockId);
    User getUserActive(Long userId);
    BaseResponseDto updateProfile(UpdateProfileRequestDto form) ;
    BaseResponseDto generatePresignedURLUpdateProfile(UploadFileRequestDto multipartFile) ;

}
