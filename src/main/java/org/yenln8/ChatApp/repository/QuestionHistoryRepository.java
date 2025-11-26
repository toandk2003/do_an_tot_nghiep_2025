package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.QuestionHistory;
import org.yenln8.ChatApp.entity.TopicTest;

@Repository
public interface QuestionHistoryRepository extends JpaRepository<QuestionHistory, Long> {
}