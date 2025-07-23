package org.yenln8.ChatApp.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.services.RedisService;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,RedisService redisService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws IOException, ServletException {

        String tokenFromRequest = getTokenFromRequest(request);
        Claims tokenDecoded = this.jwtTokenProvider.decodeToken(tokenFromRequest);

        log.info("token from Request: {}", tokenFromRequest);
        log.info("tokenDecoded: {}", tokenDecoded);

        if (tokenDecoded != null) {
            // if token expire, it's seen as invalid because decode return null
            Long id = this.jwtTokenProvider.getId(tokenDecoded);
            String email = this.jwtTokenProvider.getEmail(tokenDecoded);
            List<String> roles = this.jwtTokenProvider.getRoles(tokenDecoded);

            UserDetails userDetails = CurrentUser.builder()
                    .id(id)
                    .email(email)
                    .roles(roles)
                    .build();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // Set to SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Set last online
            this.redisService.setKey(RedisService.LAST_ONLINE_PREFIX + email,Instant.now().getEpochSecond());
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }
}