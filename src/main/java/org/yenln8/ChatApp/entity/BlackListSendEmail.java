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
@Table(name = "black_list_send_emails",
    uniqueConstraints = {
        @UniqueConstraint(name = "idx_ip_email_type", columnNames = {"from_ip_address","to_email","type"})
    }
)

public class BlackListSendEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_ip_address", nullable = false)
    private String fromIpAddress;

    @Column(name = "to_email", nullable = false)
    private String toEmail;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private TYPE type;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private STATUS status;

    @Column(name = "free_to_send_at")
    private LocalDateTime freeToSendAt;

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
        BAN,
        FREE
    }

    public enum TYPE{
        REGISTER_ACCOUNT
    }
}