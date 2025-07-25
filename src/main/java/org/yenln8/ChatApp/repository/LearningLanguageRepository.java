package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.LearningLanguage;
import org.yenln8.ChatApp.entity.NativeLanguage;

@Repository
public interface LearningLanguageRepository extends JpaRepository<LearningLanguage, Long> {
}