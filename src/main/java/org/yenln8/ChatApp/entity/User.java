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
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
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
    @JsonIgnore
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
    @JsonIgnore
    private Long deleted = 0L;

    @Version
    private Integer rowVersion;

    @OneToOne(fetch = FetchType.LAZY)
    private Profile profile;

    @OneToMany(mappedBy = "user1", fetch = FetchType.LAZY)
    private List<Friend> friendsIMade;

    @OneToMany(mappedBy = "user2", fetch = FetchType.LAZY)
    private List<Friend> friendsIReceived;

    public enum STATUS{
        NO_ONBOARDING,
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