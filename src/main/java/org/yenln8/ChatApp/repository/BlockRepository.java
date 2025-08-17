package org.yenln8.ChatApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.Block;
import org.yenln8.ChatApp.entity.Friend;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
}