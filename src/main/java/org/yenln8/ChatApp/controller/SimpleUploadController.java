package org.yenln8.ChatApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.dto.S3.DownloadFileResponseDto;
import org.yenln8.ChatApp.dto.S3.UploadFileRequestDto;
import org.yenln8.ChatApp.dto.S3.UploadFileResponseDto;
import org.yenln8.ChatApp.services.interfaces.S3Service;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("api/files/")
public class SimpleUploadController {

    @Autowired
    private S3Service s3Service;

    // Upload file
    @Operation(summary = "Upload file to S3")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadFileResponseDto> uploadFile(@ModelAttribute @Valid UploadFileRequestDto form) {
        return ResponseEntity.ok(this.s3Service.uploadFile(
                form.getFile(),
                S3Constant.AVATAR_GROUP_BUCKET,
                List.of(".png", ".jpg", ".jpeg"),
                List.of("image/png", "image/jpg", "image/jpeg"),
                 100* 1024* 1024L
        ));
    }

    // Download file
    @GetMapping("/download/{fileNameInS3}")
    @Operation(summary = "Download file from S3")
    public ResponseEntity<DownloadFileResponseDto> downloadFile(@PathVariable("fileNameInS3") String fileNameInS3) {
        return ResponseEntity.ok(this.s3Service.downloadFile(fileNameInS3, S3Constant.AVATAR_GROUP_BUCKET));
    }
}
