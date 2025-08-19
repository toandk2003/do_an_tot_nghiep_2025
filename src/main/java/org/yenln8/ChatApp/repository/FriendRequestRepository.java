package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.FriendRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query("SELECT count(fr) > 0 FROM FriendRequest fr WHERE " +
            "fr.deleted = 0 AND " +
            "fr.sender.id = :userId1 AND " +
            "fr.receiver.id = :userId2 "
    )
    boolean alreadySentFriendRequest(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT count(fr)  FROM FriendRequest fr WHERE " +
            "fr.deleted = 0 AND " +
            "fr.sender.id = :userId "
    )
    long countFriendRequestSent(@Param("userId") Long userId);

    @Query("SELECT count(fr)  FROM FriendRequest fr WHERE " +
            "fr.deleted = 0 AND " +
            "fr.receiver.id = :userId "
    )
    long countFriendRequestReceived(@Param("userId") Long userId);

    @Query("SELECT fr FROM FriendRequest fr WHERE " +
            "fr.deleted = 0 AND " +
            "fr.sender.id = :userId1 AND " +
            "fr.receiver.id = :userId2 "
    )
    FriendRequest getFriendRequestBetweenTwoUser(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT fr.receiver.id  FROM FriendRequest fr WHERE " +
            "fr.deleted = 0 AND " +
            "fr.sender.id = :userId "
    )
    List<Long> getFriendRequestsSent(@Param("userId") Long userId);

    Optional<FriendRequest> findByIdAndStatusAndDeletedAtIsNull(Long friendRequestId, FriendRequest.STATUS status);
}