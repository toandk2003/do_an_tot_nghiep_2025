package org.yenln8.ChatApp.event.synchronize;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.yenln8.ChatApp.entity.Event;

import java.io.Serializable;

@SuperBuilder
@Data
public class BaseEvent implements Serializable {
    private Event.TYPE eventType ;
    private Integer rowVersion;
}
