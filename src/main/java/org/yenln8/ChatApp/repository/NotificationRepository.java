package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}