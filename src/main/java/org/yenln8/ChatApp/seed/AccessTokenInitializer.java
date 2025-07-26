package org.yenln8.ChatApp.seed;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yenln8.ChatApp.common.util.RedisService;
import org.yenln8.ChatApp.entity.AccessToken;
import org.yenln8.ChatApp.repository.AccessTokenRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@AllArgsConstructor
@Slf4j
public class AccessTokenInitializer {
    private RedisService redisService;

    @Bean
    public CommandLineRunner initAccessToken(AccessTokenRepository accessTokenRepository) {
        return args -> {
        List<AccessToken> accessTokens = accessTokenRepository.findAllByDeletedAtIsNull();

        accessTokens.forEach(accessToken -> {
            redisService.setKeyInMinutes(accessToken.getToken(), Boolean.TRUE, Duration.between(LocalDateTime.now(), accessToken.getExpiresAt()).toMinutes());
        });
        };
    }
}
