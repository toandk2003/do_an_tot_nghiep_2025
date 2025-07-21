package org.yenln8.ChatApp.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class BlackListLoginDto {
    private Boolean ban;
    private int failTimes = 0;
}
