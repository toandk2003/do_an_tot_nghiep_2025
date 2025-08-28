package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.NativeLanguageLocale;

@Repository
public interface NativeLanguageLocaleRepository extends JpaRepository<NativeLanguageLocale, Long> {

}