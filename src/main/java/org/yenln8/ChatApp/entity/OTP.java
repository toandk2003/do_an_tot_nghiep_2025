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
@Builder
@Entity
@Table(name = "otps",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_otp_code_type_deleted", columnNames = {"otp_code", "type", "deleted"})
        }
)

public class OTP {// table chua cac OTP da gui di
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "from_ip_address", nullable = false)
    private String fromIpAddress;

    @Column(name = "to_email", nullable = false)
    private String toEmail;

    @Column(name = "otp_code", nullable = false)
    private String otpCode;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private TYPE type;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private STATUS status;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

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

    public enum STATUS{
        BE_SENT,
        VERIFIED,
        EXPIRED
    }

    public enum TYPE{
        REGISTER_ACCOUNT,
        CHANGE_PASSWORD,
        FORGOT_PASSWORD
    }

    public static String generateOTP() {
        Random random = new Random();
        int otp = 10000000 + random.nextInt(90000000); // 8 digit OTP (10000000 - 99999999)
        return String.valueOf(otp);
    }
}