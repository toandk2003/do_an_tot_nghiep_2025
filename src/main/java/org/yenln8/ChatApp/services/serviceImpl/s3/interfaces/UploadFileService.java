package org.yenln8.ChatApp.services.serviceImpl.s3.interfaces;

import org.springframework.web.multipart.MultipartFile;
import org.yenln8.ChatApp.dto.S3.UploadFileResponseDto;

public interface UploadFileService {
    UploadFileResponseDto call(MultipartFile multipartFile, String bucketName);
}
