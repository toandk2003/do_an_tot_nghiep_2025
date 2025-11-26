package org.yenln8.ChatApp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.Friend;
import org.yenln8.ChatApp.entity.Test;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    @Query("SELECT t FROM Test t " +
            "WHERE 1 = 1" +
            "AND (:topicId IS NULL OR t.topicTest.id = :topicId)" +
            "AND (:difficulty IS NULL OR t.difficultyTests.id = :difficulty)" +
            "AND (:learningLanguageId IS NULL OR t.learningLanguage.id = :learningLanguageId)"
    )
    Page<Test> getListTest(@Param("topicId") Long topicId,@Param("difficulty")  Long difficulty,@Param("learningLanguageId")  Long learningLanguageId, Pageable pageable);
}