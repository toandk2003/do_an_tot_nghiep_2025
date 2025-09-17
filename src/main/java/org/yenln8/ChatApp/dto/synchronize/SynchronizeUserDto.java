package org.yenln8.ChatApp.dto.synchronize;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SynchronizeUserDto {
    private Long userId;
    private String email;
    private String fullName;
    private String bio;
    private String location;
    private Long learningLanguageId;
    private Long nativeLanguageId;
    private String nativeLanguageName;
    private String learningLanguageName;
    private String avatar;
    private String bucket;
    private String status;
    private String role;
    private Long maxLimitResourceMedia;
    private Long currentUsageResourceMedia;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;
    private Integer rowVersion;
    private Integer deleted;
}