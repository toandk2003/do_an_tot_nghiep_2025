package org.yenln8.ChatApp.services.serviceImpl.s3.implement;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.S3.UploadFileResponseDto;
import org.yenln8.ChatApp.repository.AttachmentRepository;
import org.yenln8.ChatApp.repository.LimitResourceRepository;
import org.yenln8.ChatApp.services.serviceImpl.s3.interfaces.UploadFileService;

import java.net.URL;
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
    public UploadFileResponseDto call(MultipartFile multipartFile,String bucketName, List<String> allowedExtensions , List<String> allowedContentTypes, Long limitFileSize) {
        this.validate(multipartFile,allowedExtensions,allowedContentTypes,limitFileSize);

        return  this.save(multipartFile,bucketName);
    }

    private void validate(MultipartFile multipartFile,  List<String> allowedExtensions , List<String> allowedContentTypes,Long limitFileSize ) {
        //Validate fileSize
        long fileSize = multipartFile.getSize();
        if(fileSize <= 0 || fileSize > limitFileSize) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.file.oversize",limitFileSize));

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
    }

    private UploadFileResponseDto save(MultipartFile multipartFile, String bucketName ) {
        // Tạo tên file unique
        String originalFileName = multipartFile.getOriginalFilename();
        String contentType = multipartFile.getContentType();

        TimeBasedEpochGenerator generator = Generators.timeBasedEpochGenerator();
        String fileNameInS3 = generator.generate().toString() + "_" + originalFileName;

        // Thời gian hết hạn
        Date expiration = new Date(System.currentTimeMillis() + S3Constant.PRESIGN_URL_UPLOAD_MEDIA_EXPIRE_TIME);

        log.info("fileNameInS3: {}", fileNameInS3);
        log.info("contentType: {}", contentType);
        // Tạo presigned URL cho PUT request

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileNameInS3)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration)
                        .withContentType(contentType);
        generatePresignedUrlRequest.putCustomRequestHeader("Content-Length", String.valueOf(multipartFile.getSize() ));

        URL presignedUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

        //TODO insert attachment
        //TODO update limit resource

        return UploadFileResponseDto.builder()
                .attachmentId()
                .uploadUrl(presignedUrl.toString())
                .originalFileName(originalFileName)
                .fileNameInS3(fileNameInS3)
                .contentType(contentType)
                .size(multipartFile.getSize()/ (1024.0 * 1024.0) + " MB")
                .method("PUT")
                .expiresIn(S3Constant.PRESIGN_URL_UPLOAD_MEDIA_EXPIRE_TIME / 1000 / 60 + " minutes")
                .build();
    }
}
