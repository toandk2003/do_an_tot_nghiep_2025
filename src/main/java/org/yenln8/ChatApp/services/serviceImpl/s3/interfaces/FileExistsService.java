package org.yenln8.ChatApp.services.serviceImpl.s3.interfaces;

public interface FileExistsService {
    boolean call(String fileName, String bucketName);
}
