package org.yenln8.ChatApp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.yenln8.ChatApp.dto.other.CurrentUser;

import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws IOException, ServletException {

        String token = getTokenFromRequest(request);

        if (token == null) throw new JwtException("Invalid token");

        Claims tokenDecoded = this.jwtTokenProvider.decodeToken(token);

        log.info("tokenDecoded: {}", tokenDecoded);

        if (tokenDecoded == null) throw new JwtException("Invalid token");

        if (this.jwtTokenProvider.isExpired(tokenDecoded))
            throw new ExpiredJwtException(null, null, "Token is expired");

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

        SecurityContextHolder.getContext().setAuthentication(authentication);


        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}