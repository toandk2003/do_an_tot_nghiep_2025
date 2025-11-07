package org.yenln8.ChatApp.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.yenln8.ChatApp.event.synchronize.BaseEvent;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
 public class Notification extends BaseEvent {
    private Long id;
    private String content;
    private String senderEmail;

    @Enumerated(EnumType.STRING)
    private SENDER_TYPE senderType;

    private String receiverEmail;

    @Enumerated(EnumType.STRING)
    private RECEIVER_TYPE receiverType;

    @Enumerated(EnumType.STRING)
    private REFERENCE_TYPE referenceType;

    private String referenceEmail;

    @Enumerated(EnumType.STRING)
    private STATUS status;

    @Enumerated(EnumType.STRING)
    private TYPE type;

    private LocalDateTime seenAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long createdBy;
    private Long deleted = 0L;
    private Integer rowVersion;

    public enum STATUS {
        SEEN,
        NOT_SEEN,
        DELETED
    }

    public enum SENDER_TYPE {
        SYSTEM
    }

    public enum REFERENCE_TYPE {
        USER
    }

    public enum RECEIVER_TYPE {
        USER,
        GROUP
    }

    public enum TYPE {
        ACCEPT_FRIEND_REQUEST
    }
}