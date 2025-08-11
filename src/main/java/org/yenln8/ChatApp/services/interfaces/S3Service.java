package org.yenln8.ChatApp.services.interfaces;

import org.springframework.web.multipart.MultipartFile;
import org.yenln8.ChatApp.dto.S3.DownloadFileResponseDto;
import org.yenln8.ChatApp.dto.S3.UploadFileResponseDto;

public interface S3Service {
    UploadFileResponseDto uploadFile(MultipartFile file, String bucketName);

    DownloadFileResponseDto downloadFile(String fileNameInS3, String bucketName);

    void deleteFile(String fileName, String bucketName);

    boolean fileExists(String fileName, String bucketName);
}
