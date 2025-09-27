package org.yenln8.ChatApp.common.schedule_task;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.yenln8.ChatApp.entity.Event;
import org.yenln8.ChatApp.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class RemoveEventSchedule {
    private static final int batchSize = 100;
    private EventRepository eventRepository;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void schedule() {
        try {
            List<Event> eventToRemove = this.eventRepository.findAllByStatusAndDeletedAtLessThan(Event.STATUS.SENT, LocalDateTime.now().minusDays(15L), PageRequest.of(0, batchSize)).getContent();
            //update db
            if (!eventToRemove.isEmpty()) eventRepository.deleteAll(eventToRemove);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
