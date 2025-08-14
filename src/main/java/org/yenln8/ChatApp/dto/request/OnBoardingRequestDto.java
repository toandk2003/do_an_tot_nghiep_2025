package org.yenln8.ChatApp.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OnBoardingRequestDto {
    @Size(max = 255)
    @NotNull
    private String bio;

    @Size(max = 255)
    @NotNull
    private String location;

    @Min(1)
    @Max(Long.MAX_VALUE)
    @NotNull
    private Long nativeLanguageId;

    @Min(Long.MIN_VALUE)
    @Max(Long.MAX_VALUE)
    @NotNull
    private Long learningLanguageId;

    @Min(1)
    @Max(Long.MAX_VALUE)
    @NotNull
    private Long attachmentId;
}
