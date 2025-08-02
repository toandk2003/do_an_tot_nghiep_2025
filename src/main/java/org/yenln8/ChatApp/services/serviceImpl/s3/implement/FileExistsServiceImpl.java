package org.yenln8.ChatApp.services.serviceImpl.s3.implement;

import com.amazonaws.services.s3.AmazonS3;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.services.serviceImpl.s3.interfaces.FileExistsService;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class FileExistsServiceImpl implements FileExistsService {
    private AmazonS3 s3Client;

    @Override
    public boolean call(String fileName, String bucketName) {
        try {
            return s3Client.doesObjectExist(bucketName, fileName);
        } catch (Exception e) {
            return false;
        }
    }
}
