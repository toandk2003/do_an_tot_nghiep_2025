package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.OTP;

import java.time.LocalDateTime;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
    long countByFromIpAddressAndToEmailAndTypeAndCreatedAtAfterAndDeletedAtIsNull(String ipAddress, String email, OTP.TYPE type, LocalDateTime createdAt);
}

