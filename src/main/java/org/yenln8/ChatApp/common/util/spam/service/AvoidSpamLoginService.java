package org.yenln8.ChatApp.common.util.spam.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.common.constant.AuthConstant;
import org.yenln8.ChatApp.common.util.Network;
import org.yenln8.ChatApp.dto.redis.BlackListLoginDto;
import org.yenln8.ChatApp.dto.request.LoginRequestDto;
import org.yenln8.ChatApp.services.RedisService;

@Service
@AllArgsConstructor
@Slf4j
public class AvoidSpamLoginService {
    private RedisService redisService;

    public void call(LoginRequestDto form, HttpServletRequest request, BlackListLoginDto blackListLoginDto) {
        // key no exists
        String failToLoginKeyRedis = this.redisService.getKeyLoginWithPrefix(form.getEmail(), Network.getUserIP(request));

        if (blackListLoginDto == null) {
            this.redisService.setKeyInMinutes(failToLoginKeyRedis, BlackListLoginDto.builder()
                            .ban(Boolean.FALSE)
                            .failTimes(1)
                            .build(),
                    AuthConstant.LOGIN_TRY_AGAIN_TIME_IN_MINUTES);
        } else {
            // reach limit
            if (blackListLoginDto.getFailTimes() == AuthConstant.LOGIN_FAIL_LIMIT - 1) {
                this.redisService.setKeyInMinutes(failToLoginKeyRedis, BlackListLoginDto.builder()
                                .ban(Boolean.TRUE)
                                .build(),
                        AuthConstant.LOGIN_TRY_AGAIN_TIME_IN_MINUTES);
            } else {
                // reach limit yet
                blackListLoginDto.setFailTimes(blackListLoginDto.getFailTimes() + 1);
                this.redisService.updateValueKeepTTL(failToLoginKeyRedis, blackListLoginDto);
            }
        }
    }

}
