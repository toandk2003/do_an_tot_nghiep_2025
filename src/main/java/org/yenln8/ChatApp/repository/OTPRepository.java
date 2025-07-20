package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.OTP;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
    long countByFromIpAddressAndToEmailAndTypeAndCreatedAtAfterAndDeletedAtIsNull(String ipAddress, String email, OTP.TYPE type, LocalDateTime createdAt);

    Optional<OTP> findByOtpCodeAndTypeAndDeletedAtIsNull(String otpCode, OTP.TYPE type);
}

