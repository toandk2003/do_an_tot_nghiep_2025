package org.yenln8.ChatApp.dto.S3;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Builder(toBuilder = true)
@Data
public class UploadFileRequestDto {
    @NotNull
    private MultipartFile file;

    @NotBlank
    @Size(max = 255)
    private String checkSum;
}
