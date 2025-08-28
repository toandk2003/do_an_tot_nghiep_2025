package org.yenln8.ChatApp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.LearningLanguage;
import org.yenln8.ChatApp.entity.NativeLanguage;
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

    @Query("select u from User u " +
            "left join fetch u.profile p " +
            "left join fetch p.learningLanguage ll " +
            "left join fetch p.nativeLanguage nl " +
            "where (nl.id = :nativeLanguageId or ll.id = :nativeLanguageId or nl.id = :learningLanguageId or ll.id = :learningLanguageId) " +
            "and u.status = :status " +
            "and u.id not in (:avoidUserIds)" +
//            "and (:fullName is null or lower(u.fullName) like lower(concat('%', :fullName, '%'))) " +
            "and u.deletedAt is NULL " +
            "order by function('MD5', concat(u.id, :currentDate))"

    )
    Page<User> findByUserIdNotInAndStatusAndDeletedAtIsNullAndNotExactNativeAndLanguage(
            Long nativeLanguageId,
            Long learningLanguageId,
            User.STATUS status,
            long currentDate,
            List<Long> avoidUserIds,
            Pageable pageable
    );

    @Query("select u from User u " +
            "left join fetch u.profile p " +
            "left join fetch p.learningLanguage ll " +
            "left join fetch p.nativeLanguage nl " +
            "where (:nativeLanguageId  is null or nl.id = :nativeLanguageId) " +
            "and (:learningLanguageId is null or ll.id = :learningLanguageId)  " +
            "and u.status = :status " +
            "and u.id not in (:avoidUserIds) " +
            "and (:fullName is null or lower(u.fullName) like lower(concat('%', :fullName, '%'))) " +
            "and u.deletedAt is NULL " +
            "order by function('MD5', concat(u.id, :currentDate))"

    )
    Page<User> findByUserIdNotInAndStatusAndDeletedAtIsNullAndExactNativeAndLanguage(
            Long nativeLanguageId,
            Long learningLanguageId,
            User.STATUS status,
            long currentDate,
            List<Long> avoidUserIds,
            String fullName,
            Pageable pageable
    );

    Optional<User> findByIdAndStatus(Long userId, User.STATUS status);
}