package org.yenln8.ChatApp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.TestHistory;

@Repository
public interface TestHistoryRepository extends JpaRepository<TestHistory, Long> {
    @Query("SELECT th FROM TestHistory th " +
            "WHERE th.user.id = :userId " +
            "AND EXISTS (" +
            "SELECT t FROM Test t " +
            "WHERE 1 = 1 AND t.id = th.test.id " +
            "AND (:topicId IS NULL OR t.topicTest.id = :topicId)" +
            "AND (:difficulty IS NULL OR t.difficultyTests.id = :difficulty)" +
            "AND (:learningLanguageId IS NULL OR t.learningLanguage.id = :learningLanguageId)"
            + ")"
    )
    Page<TestHistory> getListTestHistory(@Param("userId") Long userId, @Param("topicId") Long topicId, @Param("difficulty") Long difficulty, @Param("learningLanguageId") Long learningLanguageId, Pageable pageable);
}