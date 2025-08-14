package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.Attachment;
import org.yenln8.ChatApp.entity.LimitResource;

@Repository
public interface LimitResourceRepository extends JpaRepository<LimitResource, Long> {
}