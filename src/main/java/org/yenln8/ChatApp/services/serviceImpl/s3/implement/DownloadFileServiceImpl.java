package org.yenln8.ChatApp.services.serviceImpl.s3.implement;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.S3.DownloadFileDto;
import org.yenln8.ChatApp.services.serviceImpl.s3.interfaces.DownloadFileService;

import java.net.URL;
import java.util.Date;

@Service
@AllArgsConstructor
public class DownloadFileServiceImpl implements DownloadFileService {
    private AmazonS3 s3Client;

    @Override
    public DownloadFileDto call(String fileNameInS3, String bucketName) {
            this.validateFile(fileNameInS3);

            // Kiểm tra file có tồn tại trong S3 không
            if (!s3Client.doesObjectExist(bucketName, fileNameInS3)) {
                throw new IllegalArgumentException(MessageBundle.getMessage("message.file.no.exist", fileNameInS3));
            }

            // Thời gian hết hạn
            Date expiration = new Date(System.currentTimeMillis() + S3Constant.PRESIGN_URL_DOWNLOAD_MEDIA_EXPIRE_TIME);

            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, fileNameInS3)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration);

            URL presignedUrl = s3Client.generatePresignedUrl(request);

            return DownloadFileDto.builder()
                    .downloadUrl(presignedUrl.toString())
                    .originalFileName(fileNameInS3) // Sử dụng tên file trong S3
                    .method("GET")
                    .expiresIn(S3Constant.PRESIGN_URL_DOWNLOAD_MEDIA_EXPIRE_TIME/1000/60 + " minutes")
                    .build();
    }

    private void validateFile(String fileNameInS3) {
        if(!StringUtils.hasText(fileNameInS3) ){
            throw new IllegalArgumentException(MessageBundle.getMessage("message.need.original.file.name"));
        }

        //Validate extension
        String extension = fileNameInS3.substring(fileNameInS3.lastIndexOf('.'));

        if(!StringUtils.hasText(extension) || !S3Constant.ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(MessageBundle.getMessage("message.no.support.extension.file", extension, S3Constant.ALLOWED_EXTENSIONS));
        }
    }

}
