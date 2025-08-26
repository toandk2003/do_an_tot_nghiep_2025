package org.yenln8.ChatApp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query("select u from User u " +
            "left join fetch u.profile p " +
            "left join fetch p.learningLanguage ll " +
            "left join fetch p.nativeLanguage nl " +
            "where (nl.id IN (:nativeLanguageIds) or ll.id IN (:nativeLanguageIds) or nl.id IN (:learningLanguageIds) or ll.id IN (:learningLanguageIds) ) " +
            "and u.status = :status " +
            "and u.id not in (:avoidUserIds)" +
            "and u.deletedAt is NULL " +
            "order by function('MD5', concat(u.id, :currentDate))"

    )
    Page<User> findByNativeLanguageIdAndLearningLanguageIdAndStatusAndIdNotInAndDeletedAtIsNull(
            List<Long> nativeLanguageIds,
            List<Long> learningLanguageIds,
            User.STATUS status,
            long currentDate,
            List<Long> avoidUserIds,
            Pageable pageable
    );

    @Query("select u from User u " +
            "left join fetch u.profile p " +
            "left join fetch p.learningLanguage ll " +
            "left join fetch p.nativeLanguage nl " +
            "where (nl.id IN (:nativeLanguageIds) or ll.id IN (:nativeLanguageIds) or nl.id IN (:learningLanguageIds) or ll.id IN (:learningLanguageIds) ) " +
            "and u.status = :status " +
            "and u.id not in (:avoidUserIds) " +
            "and lower(u.fullName) like lower(concat('%', :fullName, '%')) " +
            "and u.deletedAt is NULL " +
            "order by function('MD5', concat(u.id, :currentDate))"

    )
    Page<User> findByNativeLanguageIdAndLearningLanguageIdAndStatusAndIdNotInAndDeletedAtIsNullAndFullName(
            List<Long> nativeLanguageIds,
            List<Long> learningLanguageIds,
            User.STATUS status,
            long currentDate,
            List<Long> avoidUserIds,
            String fullName,
            Pageable pageable
    );

    Optional<User> findByIdAndStatus(Long userId, User.STATUS status);
}