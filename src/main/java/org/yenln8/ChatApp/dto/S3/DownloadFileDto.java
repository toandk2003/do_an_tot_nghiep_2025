package org.yenln8.ChatApp.dto.S3;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
public class DownloadFileDto {
    private String originalFileName;
    private String method;
    private String expiresIn;
    private String downloadUrl;
}
