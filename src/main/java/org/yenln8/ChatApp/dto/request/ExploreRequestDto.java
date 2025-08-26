package org.yenln8.ChatApp.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class ExploreRequestDto {
    @Min(0)
    @Max(Long.MAX_VALUE)
    @NotNull
    private Long currentPage = 0L;

    @Min(1)
    @Max(Long.MAX_VALUE)
    @NotNull
    private Long pageSize = 10L;

    @Min(1)
    @Max(Long.MAX_VALUE)
    @NotNull
    private Long learningLanguageId;

    @Min(1)
    @Max(Long.MAX_VALUE)
    @NotNull
    private Long nativeLanguageId;

    @Length(max = 255)
    private String fullName;
}
