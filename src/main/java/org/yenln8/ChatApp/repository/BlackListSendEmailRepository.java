package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.BlackListSendEmail;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlackListSendEmailRepository extends JpaRepository<BlackListSendEmail, Long> {
    Optional<BlackListSendEmail> findByFromIpAddressAndToEmailAndType(String userIP, String email, BlackListSendEmail.TYPE type);
}