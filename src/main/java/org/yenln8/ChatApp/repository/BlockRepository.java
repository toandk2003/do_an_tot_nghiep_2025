package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.Block;
import org.yenln8.ChatApp.entity.Friend;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    @Query(
            "SELECT count(b) > 0 FROM Block b " +
                    "WHERE b.deleted = 0 AND " +
                    "((b.user.id = :userId1 AND b.blockedUser.id = :userId2) " +
                    "OR (b.user.id = :userId2 AND b.blockedUser.id = :userId1))"
    )
    boolean areBlockMutualFriends(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}