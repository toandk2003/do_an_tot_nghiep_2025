package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.AccessToken;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
    List<AccessToken> findAllByDeletedAtIsNull();

    AccessToken findByToken(String token);

    Optional<AccessToken> findByTokenAndDeletedAtIsNull(String tokenFromRequest);

    List<AccessToken> findByOwnerIdAndDeletedAtIsNull(Long ownerId);
}