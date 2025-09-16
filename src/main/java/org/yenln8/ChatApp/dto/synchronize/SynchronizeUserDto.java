package org.yenln8.ChatApp.dto.synchronize;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor

public class SynchronizeUserDto {
    private String id;
    private String email;
    private String fullName;
    private String bio;
    private String location;
    private Integer learningLanguageId;
    private Integer nativeLanguageId;
    private String nativeLanguageName;
    private String learningLanguageName;
    private String avatar;
    private String bucket;
    private String status;
    private String role;
    private Integer maxLimitResourceMedia;
    private Integer currentUsageResourceMedia;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer rowVersion;
    private Integer deleted;
}