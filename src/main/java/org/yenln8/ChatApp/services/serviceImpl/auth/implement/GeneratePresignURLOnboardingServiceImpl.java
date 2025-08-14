package org.yenln8.ChatApp.services.serviceImpl.auth.implement;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.S3.UploadFileRequestDto;
import org.yenln8.ChatApp.dto.S3.UploadFileResponseDto;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.repository.*;
import org.yenln8.ChatApp.services.interfaces.S3Service;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.GeneratePresignedURLOnboarding;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class GeneratePresignURLOnboardingServiceImpl implements GeneratePresignedURLOnboarding {
    private AttachmentRepository attachmentRepository;
    private LimitResourceRepository limitResourceRepository;
    private S3Service s3Service;

    @Override
    public BaseResponseDto call(UploadFileRequestDto form, HttpServletRequest request) {
        validate(form);
        save(form);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message("Great, here is your presigned URL used to upload to S3 in onboarding feature ")
                .data(UploadFileResponseDto.builder().build())
                .build();
    }

    private void save(UploadFileRequestDto form){
        MultipartFile multipartFile = form.getFile();
        String originalFileName = multipartFile.getOriginalFilename();

    }

    private void validate(UploadFileRequestDto form) {
        MultipartFile multipartFile = form.getFile();

        // Validate fileName
        String originalFileName = multipartFile.getOriginalFilename();

        if (!StringUtils.hasText(originalFileName)) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.need.original.file.name"));
        }

        //Validate extension
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        List<String> allowedExtensions = new ArrayList<>();
        allowedExtensions.add(".jpg");
        allowedExtensions.add(".jpeg");
        allowedExtensions.add(".png");

        if (!StringUtils.hasText(extension) || !allowedExtensions.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.no.support.extension.file", extension, allowedExtensions));
        }

        //Validate contentType
        String contentType = multipartFile.getContentType();
        List<String> allowedContentTypes = new ArrayList<>();

        allowedContentTypes.add("image/jpeg");
        allowedContentTypes.add("image/jpg");
        allowedContentTypes.add("image/png");

        if (!StringUtils.hasText(contentType) || allowedContentTypes.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.no.support.content.type", contentType, allowedContentTypes));
        }
    }
}
