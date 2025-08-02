package org.yenln8.ChatApp.services.serviceImpl.s3.implement;

import com.amazonaws.services.s3.AmazonS3;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.services.serviceImpl.s3.interfaces.DeleteFileService;

@Service
@AllArgsConstructor
public class DeleteFileServiceImpl implements DeleteFileService {
    private AmazonS3 s3Client;

    @Override
    public void call(String fileName, String bucketName) {
        try {
            s3Client.deleteObject(bucketName, fileName);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file", e);
        }
    }
}
