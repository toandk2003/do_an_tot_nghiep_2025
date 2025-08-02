package org.yenln8.ChatApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.dto.S3.DownloadFileDto;
import org.yenln8.ChatApp.dto.S3.UploadFileDto;
import org.yenln8.ChatApp.services.interfaces.S3Service;

@RestController
@Slf4j
@RequestMapping("api/files/")
public class SimpleUploadController {

    @Autowired
    private S3Service s3Service;

    // Upload file
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadFileDto> uploadFile(
            @Parameter(
                    description = "File to be uploaded",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(this.s3Service.uploadFile(file, S3Constant.AVATAR_GROUP_BUCKET));
    }

    // Download file
    @GetMapping("/download/{fileNameInS3}")
    @Operation(summary = "Download file from S3")
    public ResponseEntity<DownloadFileDto> downloadFile(@PathVariable("fileNameInS3") String fileNameInS3) {
       return ResponseEntity.ok(this.s3Service.downloadFile(fileNameInS3, S3Constant.AVATAR_GROUP_BUCKET));
    }
}
