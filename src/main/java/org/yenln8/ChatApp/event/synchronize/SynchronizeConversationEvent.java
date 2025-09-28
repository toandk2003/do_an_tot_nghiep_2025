package org.yenln8.ChatApp.event.synchronize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class SynchronizeConversationEvent extends BaseEvent {
    private String name;
    private List<String> participants;
    private String type;
    private Long leader;
    private Long maxMember;
    private String avatar;
    private String bucket;
}
