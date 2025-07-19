package org.yenln8.ChatApp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@AllArgsConstructor
@Slf4j
public class RedisService {
    private ObjectMapper objectMapper;
    private RedisTemplate<String, Object> redisTemplate;

    @SuppressWarnings("NoWarning")
    public <T> T getKey(String key, Class<T> type){
        try{
            Object value = redisTemplate.opsForValue().get(key);

            if(!type.isInstance(value)) return null;

            return (T)value;
        }
        catch (Exception e){
            log.error(e.getMessage());

            return null;
        }
    }

    public <T> void setKey(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public <T> void setKey(String key, T value, long tllInSeconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(tllInSeconds));
    }
}
