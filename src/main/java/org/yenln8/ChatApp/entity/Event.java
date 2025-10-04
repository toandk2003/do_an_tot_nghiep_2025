package org.yenln8.ChatApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "events")

public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "payload", length = 1000, nullable = false)
    private String payload;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private STATUS status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public enum STATUS {
        WAIT_TO_SEND,
        SENT
    }

    public enum TYPE {
        SYNC_USER,
        SYNC_UPDATE_USER,
        SYNC_CONVERSATION,
    }
}