package org.yenln8.ChatApp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.Friend;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    @Query("SELECT COUNT(f) > 0 FROM Friend f WHERE " +
            "f.deleted = 0 AND " +
            "((f.user1.id = :userId1 AND f.user2.id = :userId2) OR " +
            " (f.user1.id = :userId2 AND f.user2.id = :userId1))")
    boolean areFriends(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT COUNT(f) > 0 FROM Friend f WHERE " +
            "f.deleted = 0 AND " +
            "((f.user1.id = :email1 AND f.user2.id = :email2) OR " +
            " (f.user1.id = :email2 AND f.user2.id = :email1))")
    boolean areFriendsByEmail(@Param("email1") String email1, @Param("email2") String email2);

    @Query("SELECT COUNT(f)  FROM Friend f WHERE " +
            "f.deleted = 0 AND " +
            "((f.user1.id = :userId ) OR " +
            " (f.user2.id = :userId))")
    Long countFriends(@Param("userId") Long userId);

    @Query("SELECT CASE " +
            "WHEN f.user1.id = :userId THEN f.user2.id " +
            "ELSE f.user1.id END " +
            "FROM Friend f WHERE " +
            "f.deleted = 0 AND " +
            "(f.user1.id = :userId OR f.user2.id = :userId)")
    List<Long> getFriendIds(@Param("userId") Long userId);

    @Query("SELECT f FROM Friend f " +
            "WHERE f.deleted = 0 " +
            "AND (" +
            "(" +
            "f.user1.id = :userId " +
            "AND (:fullName IS NULL OR LOWER(f.user2.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
            "AND (:learningLanguageId IS NULL OR f.user2.profile.learningLanguage.id = :learningLanguageId) " +
            "AND (:nativeLanguageId IS NULL OR f.user2.profile.nativeLanguage.id = :nativeLanguageId)" +
            ") " +
            "OR " +
            "(" +
            "f.user2.id = :userId " +
            "AND (:fullName IS NULL OR LOWER(f.user1.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
            "AND (:learningLanguageId IS NULL OR f.user1.profile.learningLanguage.id = :learningLanguageId) " +
            "AND (:nativeLanguageId IS NULL OR f.user1.profile.nativeLanguage.id = :nativeLanguageId)" +
            ")" +
            ")")
    Page<Friend> getFriends(@Param("userId") Long userId, Long learningLanguageId, Long nativeLanguageId, String fullName, Pageable pageable);


    Optional<Friend> findByIdAndDeletedAtIsNull(Long friendId);
}