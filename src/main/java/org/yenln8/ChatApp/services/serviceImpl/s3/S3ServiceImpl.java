package org.yenln8.ChatApp.services.serviceImpl.s3;

import com.amazonaws.services.s3.AmazonS3;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.dto.S3.DownloadFileResponseDto;
import org.yenln8.ChatApp.dto.S3.UploadFileResponseDto;
import org.yenln8.ChatApp.services.interfaces.S3Service;
import org.yenln8.ChatApp.services.serviceImpl.s3.interfaces.DeleteFileService;
import org.yenln8.ChatApp.services.serviceImpl.s3.interfaces.DownloadFileService;
import org.yenln8.ChatApp.services.serviceImpl.s3.interfaces.FileExistsService;
import org.yenln8.ChatApp.services.serviceImpl.s3.interfaces.UploadFileService;

@Service
@AllArgsConstructor
public class S3ServiceImpl implements S3Service {
    private AmazonS3 s3Client;

    private DeleteFileService deleteFileService;
    private UploadFileService uploadFileService;
    private DownloadFileService downloadFileService;
    private FileExistsService fileExistsService;

    @Override
    public UploadFileResponseDto uploadFile(MultipartFile file, String checkSum, String bucketName) {
        return this.uploadFileService.call(file, checkSum, bucketName);
    }

    @Override
    public DownloadFileResponseDto downloadFile(String fileNameInS3, String bucketName) {
        return this.downloadFileService.call(fileNameInS3, bucketName);
    }

    @Override
    public void deleteFile(String fileName, String bucketName) {
        this.deleteFileService.call(fileName, bucketName);
    }

    @Override
    public boolean fileExists(String fileName, String bucketName) {
        return this.fileExistsService.call(fileName, bucketName);
    }

    @PostConstruct
    public void init() {
        // Tạo bucket nếu chưa có
        this.initBucket(S3Constant.AVATAR_PRIVATE_BUCKET);
        this.initBucket(S3Constant.COUNTRY_BUCKET);
        this.initBucket(S3Constant.CHAT_MEDIA_BUCKET);
        this.initBucket(S3Constant.AVATAR_GROUP_BUCKET);
    }

    public void initBucket(String bucketName) {
        try {
            if (!s3Client.doesBucketExistV2(bucketName)) {
                s3Client.createBucket(bucketName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating bucket: " + bucketName, e);
        }
    }
}
