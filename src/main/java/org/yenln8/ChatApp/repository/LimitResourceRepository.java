package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.LimitResource;

import java.util.Optional;

@Repository
public interface LimitResourceRepository extends JpaRepository<LimitResource, Long> {
    Optional<LimitResource> findByUserId(Long userId);
}