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
@Builder
@Entity
@Table(name = "attachments")

public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "file_name_in_s3", nullable = false)
    private String fileNameInS3;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "s3_bucket_name", nullable = false)
    private String s3BucketName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TYPE type;

    @CreationTimestamp
    @Column(name = "expire_at", updatable = false, nullable = false)
    private LocalDateTime expireAt;

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

    @Builder.Default
    @Column(name = "deleted", columnDefinition = "BIGINT DEFAULT 0", nullable = false)
    @JsonIgnore
    private Long deleted = 0L;

    @Version
    private Integer rowVersion;

    public enum TYPE {
        IMAGE,
        VIDEO,
        DOCUMENT
    }

    public enum STATUS {
        WAITING_CONFIRM,
        CONFIRMED
    }
}