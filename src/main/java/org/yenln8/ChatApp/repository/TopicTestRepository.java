package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.Profile;
import org.yenln8.ChatApp.entity.TopicTest;

@Repository
public interface TopicTestRepository extends JpaRepository<TopicTest, Long> {
}