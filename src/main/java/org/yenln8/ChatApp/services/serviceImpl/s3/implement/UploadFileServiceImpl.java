package org.yenln8.ChatApp.services.serviceImpl.s3.implement;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.S3.UploadFileResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.entity.Attachment;
import org.yenln8.ChatApp.entity.LimitResource;
import org.yenln8.ChatApp.repository.AttachmentRepository;
import org.yenln8.ChatApp.repository.LimitResourceRepository;
import org.yenln8.ChatApp.services.serviceImpl.s3.interfaces.UploadFileService;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UploadFileServiceImpl implements UploadFileService {
    private AmazonS3 s3Client;
    private AttachmentRepository attachmentRepository;
    private LimitResourceRepository limitResourceRepository;

    @Override
    public UploadFileResponseDto call(MultipartFile multipartFile, String bucketName, List<String> allowedExtensions, List<String> allowedContentTypes, Long limitFileSize) {
        try {
            Authentication securityContextHolder = SecurityContextHolder.getContext().getAuthentication();
            CurrentUser currentUser = (CurrentUser) securityContextHolder.getPrincipal();

            LimitResource limitResource = this.validate(currentUser, multipartFile, allowedExtensions, allowedContentTypes, limitFileSize);

            return this.save(currentUser, limitResource, multipartFile, bucketName);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private LimitResource validate(CurrentUser currentUser, MultipartFile multipartFile, List<String> allowedExtensions, List<String> allowedContentTypes, Long limitFileSize) {
        Long userId = currentUser.getId();

        //Validate fileSize
        long fileSize = multipartFile.getSize();
        if (fileSize <= 0 || fileSize > limitFileSize) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.file.over.size", limitFileSize, fileSize));

        }
        // Validate fileName
        String originalFileName = multipartFile.getOriginalFilename();

        if (!StringUtils.hasText(originalFileName)) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.need.original.file.name"));
        }

        //Validate extension
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));

        if (!StringUtils.hasText(extension) || !allowedExtensions.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.no.support.extension.file", extension, allowedExtensions));
        }

        //Validate contentType
        String contentType = multipartFile.getContentType();

        if (!StringUtils.hasText(contentType) || !allowedContentTypes.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.no.support.content.type", contentType, allowedContentTypes));
        }

        // Validate if user exceed limit resource
        LimitResource limitResource = this.limitResourceRepository
                .findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(MessageBundle.getMessage("error.object.not.found", "LimitResource", "userId", userId)));

        //Reset resource usage in new day
        long nowDay = LocalDateTime.now().toLocalDate().toEpochDay();
        long updateLimitResourceDay = limitResource.getUpdatedAt().toLocalDate().toEpochDay();

        if (nowDay > updateLimitResourceDay) limitResource.setCurrentUsage(0L);

        // validate exceed limit
        if (limitResource.getCurrentUsage() + fileSize > S3Constant.MAX_LIMIT_RESOURCE) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.error.resource.limit"));
        }

        return limitResource;
    }

    private UploadFileResponseDto save(CurrentUser currentUser, LimitResource limitResource, MultipartFile multipartFile, String bucketName) {

        // Tạo tên file unique
        String originalFileName = multipartFile.getOriginalFilename();
        String contentType = multipartFile.getContentType();
        Long fileSize = multipartFile.getSize();

        TimeBasedEpochGenerator generator = Generators.timeBasedEpochGenerator();
        String fileNameInS3 = generator.generate().toString() + "_" + originalFileName;

        // Thời gian hết hạn
        Date expiration = new Date(System.currentTimeMillis() + S3Constant.PRESIGN_URL_UPLOAD_MEDIA_EXPIRE_TIME);

        log.info("fileNameInS3: {}", fileNameInS3);
        log.info("contentType: {}", contentType);
        log.info("fileSize: {}", fileSize);

        // Tạo presigned URL cho PUT request

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileNameInS3)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration)
                        .withContentType(contentType);
        generatePresignedUrlRequest.putCustomRequestHeader("Content-Length", String.valueOf(fileSize));

        URL presignedUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

        // insert attachment - no ownerId
        Attachment attachmentToSave = Attachment.builder()
                .originalFileName(originalFileName)
                .s3BucketName(bucketName)
                .fileNameInS3(fileNameInS3)
                .fileSize(fileSize)
                .contentType(contentType)
                .status(Attachment.STATUS.WAITING_CONFIRM)
                .expireAt(LocalDateTime.now().plusDays(S3Constant.EXPIRE_TIME_ATTACHMENT))
                .createdBy(currentUser.getId())
                .build();
        this.attachmentRepository.save(attachmentToSave);

        // update limit resource
        limitResource.setCurrentUsage(limitResource.getCurrentUsage() + fileSize);
        this.limitResourceRepository.save(limitResource);

        return UploadFileResponseDto.builder()
                .attachmentId(attachmentToSave.getId())
                .uploadUrl(presignedUrl.toString())
                .originalFileName(originalFileName)
                .fileNameInS3(fileNameInS3)
                .contentType(contentType)
                .size(multipartFile.getSize() / (1024.0 * 1024.0) + " MB")
                .method("PUT")
                .expiresIn(S3Constant.PRESIGN_URL_UPLOAD_MEDIA_EXPIRE_TIME / 1000 / 60 + " minutes")
                .build();
    }
}
