package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.NativeLanguage;
import org.yenln8.ChatApp.entity.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
}