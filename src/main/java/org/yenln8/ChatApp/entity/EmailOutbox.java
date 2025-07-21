package org.yenln8.ChatApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Random;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "email_outboxs")

public class EmailOutbox {// table chua cac OTP da gui di
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "to_email" , nullable = false)
    private String toEmail;

    @Column(name = "otp_code" , nullable = false)
    private String otpCode;

    @Column(name = "type" , nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private TYPE type;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted", columnDefinition = "BIGINT DEFAULT 0")
    private Long deleted = 0L;

    @Version
    private Integer rowVersion;

    public enum TYPE {
        REGISTER_ACCOUNT,
    }
}