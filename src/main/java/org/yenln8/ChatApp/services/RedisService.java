package org.yenln8.ChatApp.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@AllArgsConstructor
@Slf4j
public class RedisService {
    public static final String BLACKLIST_LOGIN_PREFIX = "BLACK_LIST_LOGIN_PREFIX_";
    public static final String LAST_ONLINE_PREFIX = "LAST_ONLINE_PREFIX_";

    private RedisTemplate<String, Object> redisTemplate;

    @SuppressWarnings("NoWarning")
    public <T> T getKey(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(key);

            return (T) value;
        } catch (Exception e) {
            log.error(e.getMessage());

            return null;
        }
    }

    public long getExpireTimeBySecond(String key) {
        return redisTemplate.getExpire(key);
    }

    public <T> void setKey(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public <T> void setKeyInMinutes(String key, T value, long tllInMinute) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(tllInMinute));
    }

    public boolean deleteKey(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Error deleting key {}: {}", key, e.getMessage());
            return false;
        }
    }

    public boolean extendTTLBy(String key, long additionalSeconds) {
        try {
            long currentTtl = redisTemplate.getExpire(key); // TTL hiện tại (tính bằng giây)
            if (currentTtl < 0) {
                log.warn("Key {} does not have a valid TTL to extend.", key);
                return false;
            }

            return Boolean.TRUE.equals(
                    redisTemplate.expire(key, Duration.ofSeconds(currentTtl + additionalSeconds))
            );
        } catch (Exception e) {
            log.error("Error extending TTL for key {}: {}", key, e.getMessage());
            return false;
        }
    }

    public void updateValueKeepTTL(String key, Object newValue) {
        long ttlInMilisSecond = redisTemplate.getExpire(key);
        this.redisTemplate.opsForValue().set(key, newValue, Duration.ofSeconds(ttlInMilisSecond));
    }

    public String getKeyLoginWithPrefix(String email, String ipAddress) {
        return BLACKLIST_LOGIN_PREFIX + email + "_" + ipAddress;
    }

    public String getKeyLastOnlineWithPrefix(String email) {
        return LAST_ONLINE_PREFIX + email;
    }
}
