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
@Table(name = "password_pendings")

public class PasswordPending {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "otp_id", nullable = false)
    private Long otpId;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "new_password", nullable = false)
    private String newPassword;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TYPE type;

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
    private Long deleted = 0L;

    @Version
    private Integer rowVersion;

    public enum TYPE{
        FORGOT,
        CHANGE,
    }

    public enum STATUS{
        PENDING,
        DONE,
        EXPIRED
    }
}