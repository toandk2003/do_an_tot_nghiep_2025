package org.yenln8.ChatApp.services.serviceImpl.s3.interfaces;

import org.yenln8.ChatApp.dto.S3.DownloadFileDto;

public interface DownloadFileService {

    DownloadFileDto call(String fileNameInS3, String bucketName);
}
