package org.yenln8.ChatApp.dto.synchronize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SynchronizeConversationDto {
    private String name;
    private List<Long> participants;
    private String type;
    private Long leader;
    private Long maxMember;
    private String avatar;
    private String bucket;
}
