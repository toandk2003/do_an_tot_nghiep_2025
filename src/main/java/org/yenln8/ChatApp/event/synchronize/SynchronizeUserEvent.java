package org.yenln8.ChatApp.event.synchronize;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class SynchronizeUserEvent extends BaseEvent {
    private String userFullName;
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
    private Integer deleted;
}