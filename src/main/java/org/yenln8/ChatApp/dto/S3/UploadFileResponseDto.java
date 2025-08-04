package org.yenln8.ChatApp.dto.S3;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
public class UploadFileResponseDto {
    private String originalFileName;
    private String fileNameInS3;
    private String size;
    private String contentType;
    private String method;
    private String expiresIn;
    private String uploadUrl;
}
