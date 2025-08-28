package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.NativeLanguage;

import java.util.List;

@Repository
public interface NativeLanguageRepository extends JpaRepository<NativeLanguage, Long> {
}