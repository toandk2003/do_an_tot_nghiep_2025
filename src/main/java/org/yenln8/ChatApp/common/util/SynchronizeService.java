package org.yenln8.ChatApp.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.entity.Event;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SynchronizeService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper; // dùng bean chung thay vì new

    /**
     * Cho phép truyền thẳng DTO hoặc Map đều được
     */
    public void publish(Event event) {
        String destination = event.getDestination();
        String payload = event.getPayload();
        try {
            Map<String, String> field = new HashMap<>();
            field.put("data", objectMapper.writeValueAsString(payload));
            field.put("sentAt", LocalDateTime.now().toString());

            redisTemplate.opsForStream()
                    .add(destination, field);

            log.info("Published notification to stream: {} with payload: {}",
                    destination, objectMapper.writeValueAsString(payload));

        } catch (Exception e) {
            log.error("Error publishing to stream: {}", destination);
            throw new RuntimeException("Failed to publish notification", e);
        }
    }
}
