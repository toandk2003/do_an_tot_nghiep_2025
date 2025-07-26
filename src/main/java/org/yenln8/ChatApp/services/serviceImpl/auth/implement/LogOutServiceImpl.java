package org.yenln8.ChatApp.services.serviceImpl.auth.implement;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.common.util.RedisService;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.entity.AccessToken;
import org.yenln8.ChatApp.repository.AccessTokenRepository;
import org.yenln8.ChatApp.services.serviceImpl.auth.interfaces.LogOutService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class LogOutServiceImpl implements LogOutService {
    private AccessTokenRepository accessTokenRepository;
    private RedisService redisService;

    @Override
    public BaseResponseDto call(HttpServletRequest request) {
        String tokenFromRequest = getTokenFromRequest(request) == null ? "" : getTokenFromRequest(request);

        this.save(tokenFromRequest);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message(MessageBundle.getMessage("logout.success"))
                .build();
    }

    private void save(String tokenFromRequest) {
        //update db
        AccessToken accessToken = this.accessTokenRepository.findByTokenAndDeletedAtIsNull(tokenFromRequest).orElse(null);

        if (accessToken == null) return;

        this.accessTokenRepository.save(accessToken.toBuilder()
                .deletedAt(LocalDateTime.now())
                .deleted(accessToken.getId())
                .build());

        // delete redis key
        this.redisService.deleteKey(tokenFromRequest);
    }

    @Override
    public void logOutAllDevice(Long userId) {
        //update db
        List<AccessToken> accessTokens = this.accessTokenRepository.findByOwnerIdAndDeletedAtIsNull(userId);

        accessTokens.forEach(accessToken -> {
            String token = accessToken.getToken();
            // update db
            this.accessTokenRepository.save(accessToken.toBuilder()
                    .deletedAt(LocalDateTime.now())
                    .deleted(accessToken.getId())
                    .build());

            // delete key redis
            this.redisService.deleteKey(token);

        });

    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }
}
