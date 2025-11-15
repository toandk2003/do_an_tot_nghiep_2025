package org.yenln8.ChatApp.event.synchronize;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.yenln8.ChatApp.entity.Event;

import java.io.Serializable;
import java.util.UUID;

@SuperBuilder
@Data
public class BaseEvent implements Serializable {
    private Event.TYPE eventType ;
    private Integer rowVersion;
    @Builder.Default
    private String checkDuplicate = UUID.randomUUID().toString();
}
