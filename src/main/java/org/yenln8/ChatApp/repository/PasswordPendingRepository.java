package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.PasswordPending;
import org.yenln8.ChatApp.entity.Profile;

import java.util.Optional;

@Repository
public interface PasswordPendingRepository extends JpaRepository<PasswordPending, Long> {
    Optional<PasswordPending> findByOtpIdAndStatusAndDeletedAtIsNull(Long id, PasswordPending.STATUS status);
}