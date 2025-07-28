package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    @Query("select distinct u from  User u " +
            "left join fetch  u.profile p " +
            "left join fetch p.learningLanguage  ll " +
            "left join fetch p.nativeLanguage nl " +
            "where u.id = :id " +
            "and u.deleted = 0 ")
    Optional<User> findByUserIdWithProfileAndNativeAndLearning(Long id);
}