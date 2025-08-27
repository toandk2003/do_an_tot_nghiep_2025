package org.yenln8.ChatApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "notifications")

public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "sender_type")
    @Enumerated(EnumType.STRING)
    private SENDER_TYPE senderType;

    @Column(name = "receiver_id")
    private Long receiverId;

    @Column(name = "receive_type")
    @Enumerated(EnumType.STRING)
    private RECEIVER_TYPE receiverType;

    @Column(name = "reference_type")
    @Enumerated(EnumType.STRING)
    private REFERENCE_TYPE referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private STATUS status;

    @UpdateTimestamp
    @Column(name = "seen_at")
    private LocalDateTime seenAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Builder.Default
    @Column(name = "deleted", columnDefinition = "BIGINT DEFAULT 0", nullable = false)
    @JsonIgnore
    private Long deleted = 0L;

    @Version
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
        USER
    }
}