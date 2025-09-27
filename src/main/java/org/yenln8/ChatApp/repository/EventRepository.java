package org.yenln8.ChatApp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yenln8.ChatApp.entity.Event;

import java.net.ContentHandler;
import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findAllByStatus(Event.STATUS status, Pageable pageable);
    Page<Event>  findAllByStatusAndDeletedAtLessThan(Event.STATUS status, LocalDateTime localDateTime, Pageable pageable);
}