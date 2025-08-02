package org.yenln8.ChatApp.services.serviceImpl.s3.implement;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.S3.UploadFileDto;
import org.yenln8.ChatApp.services.serviceImpl.s3.interfaces.UploadFileService;

import java.net.URL;
import java.nio.file.Files;
import java.util.Date;

@Service
@Slf4j
@AllArgsConstructor
public class UploadFileServiceImpl implements UploadFileService {
    private AmazonS3 s3Client;

    @Override
    public UploadFileDto call(MultipartFile multipartFile, String bucketName) {
        this.validateFile(multipartFile);

        // Tạo tên file unique
        String originalFileName = multipartFile.getOriginalFilename();
        String contentType = multipartFile.getContentType();

        TimeBasedEpochGenerator generator = Generators.timeBasedEpochGenerator();
        String fileNameInS3 = generator.generate().toString() + "_" + originalFileName;

        // Thời gian hết hạn
        Date expiration = new Date(System.currentTimeMillis() + S3Constant.PRESIGN_URL_UPLOAD_MEDIA_EXPIRE_TIME);

        // Tạo presigned URL cho PUT request
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileNameInS3)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration)
                        .withContentType(contentType);

        log.info("fileNameInS3: {}", fileNameInS3);
        log.info("contentType: {}", contentType);

        URL presignedUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        System.out.println("presignedUrl: " + presignedUrl);
        return UploadFileDto.builder()
                .uploadUrl(presignedUrl.toString())
                .originalFileName(originalFileName)
                .fileNameInS3(fileNameInS3)
                .contentType(contentType)
                .size(multipartFile.getSize()/ (1024.0 * 1024.0) + " MB")
                .method("PUT")
                .expiresIn(S3Constant.PRESIGN_URL_UPLOAD_MEDIA_EXPIRE_TIME / 1000 / 60 + " minutes")
                .build();
    }

    private void validateFile(MultipartFile multipartFile) {
        // TODO need add interceptor to limit file size before access Controller
        // Validate fileName
        String originalFileName = multipartFile.getOriginalFilename();

        if (!StringUtils.hasText(originalFileName)) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.need.original.file.name"));
        }

        //Validate extension
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));

        if (!StringUtils.hasText(extension) || !S3Constant.ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.no.support.extension.file", extension, S3Constant.ALLOWED_EXTENSIONS));
        }

        //Validate contentType
        String contentType = multipartFile.getContentType();

        if (!StringUtils.hasText(contentType) || !S3Constant.ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.no.support.content.type", contentType, S3Constant.ALLOWED_CONTENT_TYPES));
        }
    }
}
