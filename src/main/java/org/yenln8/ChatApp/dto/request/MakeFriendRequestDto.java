package org.yenln8.ChatApp.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class MakeFriendRequestDto {

    @Min(1)
    @Max(Long.MAX_VALUE)
    @NotNull
    private Long receiverId ;
}
