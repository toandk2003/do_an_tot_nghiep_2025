package org.yenln8.ChatApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yenln8.ChatApp.dto.request.LearningLanguageMiniDto;
import org.yenln8.ChatApp.dto.request.NativeLanguageMiniDto;
import org.yenln8.ChatApp.entity.LearningLanguage;
import org.yenln8.ChatApp.entity.NativeLanguage;
import org.yenln8.ChatApp.entity.Profile;
import org.yenln8.ChatApp.entity.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class GetProfileResponseDto implements Serializable {
    private Long id;
    private String email;
    private String fullName;
    private String bio;
    private String location;
    private Boolean isOnboarded;
    private NativeLanguageMiniDto nativeLanguage;
    private LearningLanguageMiniDto learningLanguage;
    private Long attachmentId;
    private String fileNameInS3;
    private String profilePic;
    private LocalDateTime lastOnline;
    private Integer rowVersion;
}
