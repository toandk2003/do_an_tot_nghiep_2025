package org.yenln8.ChatApp.services.serviceImpl.auth.implement;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yenln8.ChatApp.common.constant.FileConstant;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.dto.S3.UploadFileRequestDto;
import org.yenln8.ChatApp.dto.S3.UploadFileResponseDto;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.repository.AttachmentRepository;
import org.yenln8.ChatApp.repository.LimitResourceRepository;
import org.yenln8.ChatApp.services.interfaces.S3Service;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.GeneratePresignedURLOnboarding;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class GeneratePresignURLOnboardingServiceImpl implements GeneratePresignedURLOnboarding {
    private S3Service s3Service;

    @Override
    public BaseResponseDto call(UploadFileRequestDto form, HttpServletRequest request) {
        UploadFileResponseDto responseDto = save(form);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Great, here is your presigned URL used to upload to S3 in onboarding feature ")
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
