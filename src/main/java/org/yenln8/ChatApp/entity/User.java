package org.yenln8.ChatApp.entity;

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
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_email_deleted", columnNames = {"email", "deleted"})
        },
        indexes = {
                @Index(name = "idx_email_deleted", columnList = "email, deleted")
        })

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private STATUS status;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private ROLE role;

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
    private Long deleted = 0L;

    @Version
    private Integer rowVersion;

    @OneToOne(mappedBy = "user", cascade =  CascadeType.ALL)
    private Profile profile;

    public enum STATUS{
        INACTIVE,
        ACTIVE,
        LOCK,
        BAN
    }

    public enum ROLE{
        USER,
        ADMIN
    }
}