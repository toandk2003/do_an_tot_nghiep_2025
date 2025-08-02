package org.yenln8.ChatApp.services.serviceImpl.s3.interfaces;

public interface DeleteFileService {
    void call(String fileName, String bucketName);
}
