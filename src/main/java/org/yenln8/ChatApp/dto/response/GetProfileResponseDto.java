package org.yenln8.ChatApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yenln8.ChatApp.entity.LearningLanguage;
import org.yenln8.ChatApp.entity.NativeLanguage;
import org.yenln8.ChatApp.entity.Profile;
import org.yenln8.ChatApp.entity.User;

import java.io.Serializable;
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
    private NativeLanguage nativeLanguage;
    private LearningLanguage learningLanguage;
}
