package org.yenln8.ChatApp.services.serviceImpl.s3.interfaces;

import org.springframework.web.multipart.MultipartFile;
import org.yenln8.ChatApp.dto.S3.UploadFileResponseDto;

import java.util.List;

public interface UploadFileService {
    UploadFileResponseDto call(MultipartFile file, String bucketName, List<String> allowedExtensions , List<String> allowedContentTypes, Long limitFileSize);
}
