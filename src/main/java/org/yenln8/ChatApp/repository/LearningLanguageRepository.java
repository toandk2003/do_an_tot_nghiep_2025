package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.LearningLanguage;

import java.util.List;

@Repository
public interface LearningLanguageRepository extends JpaRepository<LearningLanguage, Long> {
    List<LearningLanguage> findAllByLocale(LearningLanguage.LOCALE locale);

    List<LearningLanguage> findAllByCode(LearningLanguage.CODE learningLanguageCode);
}