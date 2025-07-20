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
@Table(name = "account_pendings")

public class AccountPending {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "otp_id", nullable = false)
    private Long otpId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private STATUS status;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ROLE role;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted", columnDefinition = "INT DEFAULT 0")
    private Integer deleted = 0;

    @Version
    private Integer rowVersion;

    public enum STATUS{
        PENDING,
        ACTIVE
    }

    public enum ROLE{
        USER,
        ADMIN
    }
}