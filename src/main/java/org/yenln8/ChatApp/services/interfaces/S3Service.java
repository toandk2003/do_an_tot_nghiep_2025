package org.yenln8.ChatApp.services.interfaces;

import org.springframework.web.multipart.MultipartFile;
import org.yenln8.ChatApp.dto.S3.DownloadFileDto;
import org.yenln8.ChatApp.dto.S3.UploadFileDto;

public interface S3Service {
    UploadFileDto uploadFile(MultipartFile file, String bucketName);

    DownloadFileDto downloadFile(String fileNameInS3, String bucketName);

    void deleteFile(String fileName, String bucketName);

    boolean fileExists(String fileName, String bucketName);
}
