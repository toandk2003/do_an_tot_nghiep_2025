package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.AccessToken;
import org.yenln8.ChatApp.entity.User;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
}