package org.yenln8.ChatApp.common.schedule_task;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.yenln8.ChatApp.common.util.SynchronizeService;
import org.yenln8.ChatApp.entity.Event;
import org.yenln8.ChatApp.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class SendEventSchedule {
    private static final int batchSize = 100;
    private EventRepository eventRepository;
    private SynchronizeService synchronizeService;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void schedule() {
        try {
            List<Event> eventToSend = this.eventRepository.findAllByStatus(Event.STATUS.WAIT_TO_SEND, PageRequest.of(0, batchSize)).getContent();
            for (Event event : eventToSend) {
                //update db
                event.setStatus(Event.STATUS.SENT);
                event.setUpdatedAt(LocalDateTime.now());
                event.setDeletedAt(LocalDateTime.now());
                eventRepository.save(event);
                //publish
                synchronizeService.publish(event);

            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
