package org.yenln8.ChatApp.services.serviceImpl.user.implement;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yenln8.ChatApp.common.constant.FileConstant;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.dto.S3.UploadFileRequestDto;
import org.yenln8.ChatApp.dto.S3.UploadFileResponseDto;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.response.UnBlockResponseDto;
import org.yenln8.ChatApp.entity.Block;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.BlockRepository;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.interfaces.S3Service;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GeneratePresignedURLUpdateProfileService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.UpdateProfileService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class GeneratePresignedURLUpdateProfileServiceImpl implements GeneratePresignedURLUpdateProfileService {
    private S3Service s3Service;

    @Override
    public BaseResponseDto call(UploadFileRequestDto form) {
        UploadFileResponseDto responseDto = save(form);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Great, here is your presigned URL used to upload to S3 in update profile feature ")
                .data(responseDto)
                .build();

    }

    private UploadFileResponseDto save(UploadFileRequestDto form) {
        MultipartFile multipartFile = form.getFile();

        String bucket = S3Constant.AVATAR_PRIVATE_BUCKET;

        List<String> allowedExtensions = FileConstant.ALLOW_EXTENSION_AVATAR_FILE;

        List<String> allowedContentTypes = FileConstant.ALLOW_CONTENT_TYPE_AVATAR_FILE;

        Long limitAvatarFileSize = S3Constant.MAX_FILE_SIZE_AVATAR;

        return this.s3Service.uploadFile(multipartFile, bucket, allowedExtensions, allowedContentTypes, limitAvatarFileSize);
    }
}
