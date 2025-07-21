package org.yenln8.ChatApp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.EmailOutbox;

@Repository
public interface EmailOutboxRepository extends JpaRepository<EmailOutbox, Long> {
    Page<EmailOutbox> findAllByTypeAndDeletedAtIsNull(EmailOutbox.TYPE type, Pageable pageable);
}

