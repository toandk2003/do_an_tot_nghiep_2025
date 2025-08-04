package org.yenln8.ChatApp.services.serviceImpl.s3.interfaces;

import org.yenln8.ChatApp.dto.S3.DownloadFileResponseDto;

public interface DownloadFileService {

    DownloadFileResponseDto call(String fileNameInS3, String bucketName);
}
