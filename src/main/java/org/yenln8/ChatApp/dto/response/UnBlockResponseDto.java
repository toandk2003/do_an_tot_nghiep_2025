package org.yenln8.ChatApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class UnBlockResponseDto {
    private Long id;
    private GetProfileResponseDto user;
    private GetProfileResponseDto blockedUser;
}
