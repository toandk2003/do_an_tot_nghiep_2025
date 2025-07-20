package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.AccountPending;
import org.yenln8.ChatApp.entity.User;

import java.util.Optional;

@Repository
public interface AccountPendingRepository extends JpaRepository<AccountPending, Long> {
    Optional<AccountPending> findByOtpIdAndStatusAndDeletedAtIsNull(Long id, AccountPending.STATUS status);
}