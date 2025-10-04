package org.yenln8.ChatApp.event.synchronize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class SynchronizeUpdateUserEvent extends BaseEvent {
    private String email;
    private String bio;
    private String location;
    private Long learningLanguageId;
    private Long nativeLanguageId;
    private String avatar;
    private String bucket;
}