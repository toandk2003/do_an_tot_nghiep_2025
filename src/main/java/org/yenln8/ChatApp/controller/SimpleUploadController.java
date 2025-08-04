package org.yenln8.ChatApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.dto.S3.DownloadFileResponseDto;
import org.yenln8.ChatApp.dto.S3.UploadFileRequestDto;
import org.yenln8.ChatApp.dto.S3.UploadFileResponseDto;
import org.yenln8.ChatApp.services.interfaces.S3Service;

@RestController
@Slf4j
@RequestMapping("api/files/")
public class SimpleUploadController {

    @Autowired
    private S3Service s3Service;

    // Upload file
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadFileResponseDto> uploadFile( @ModelAttribute @Valid UploadFileRequestDto form) {
        log.info("xinchao " + form.getCheckSum());
        return ResponseEntity.ok(this.s3Service.uploadFile(form.getFile(), form.getCheckSum(), S3Constant.AVATAR_GROUP_BUCKET));
    }

    // Download file
    @GetMapping("/download/{fileNameInS3}")
    @Operation(summary = "Download file from S3")
    public ResponseEntity<DownloadFileResponseDto> downloadFile(@PathVariable("fileNameInS3") String fileNameInS3) {
        return ResponseEntity.ok(this.s3Service.downloadFile(fileNameInS3, S3Constant.AVATAR_GROUP_BUCKET));
    }
}
