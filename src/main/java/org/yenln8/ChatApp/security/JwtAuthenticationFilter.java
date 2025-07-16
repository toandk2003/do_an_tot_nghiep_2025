package org.yenln8.ChatApp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.yenln8.ChatApp.common.util.MessageBundle;
import org.yenln8.ChatApp.dto.other.CurrentUser;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private JwtTokenProvider jwtTokenProvider;
    @Value(value = "${jwt.skip-endpoints}")
    private List<String> skipEndpoints;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,  List<String> skipEndpoints) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.skipEndpoints = skipEndpoints;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws IOException, ServletException {

        String tokenFromRequest = getTokenFromRequest(request);
        Claims tokenDecoded = this.jwtTokenProvider.decodeToken(tokenFromRequest);


        log.info("token from Request: " + tokenFromRequest);
        log.info("tokenDecoded: {}",tokenDecoded);

        if (tokenDecoded == null) {
            // if token expire, it's seen as invalid because decode return null
            log.error("tokenDecoded is null");
            handleAuthenticationError(response, MessageBundle.getMessage("validate.token.invalid"), HttpStatus.UNAUTHORIZED);
            return;
        }

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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        log.info("path: {}", path);
        log.info("skip endpoints: {}", skipEndpoints);
        // Kiểm tra exact match
        if (skipEndpoints.contains(path)) {
            return true;
        }

        // Kiểm tra prefix match
        return skipEndpoints.stream()
                .anyMatch(path::startsWith);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }

    private void handleAuthenticationError(HttpServletResponse response, String message, HttpStatus statusCode) throws IOException {
        response.setStatus(statusCode.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
                "{\"error\":\"%s\",\"message\":\"%s\",\"status\":%d,\"timestamp\":\"%s\"}",
                getErrorNameByStatus(statusCode),
                message,
                statusCode.value(),
                java.time.Instant.now()
        );

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    private String getErrorNameByStatus(HttpStatus statusCode) {
        return switch (statusCode.value()) {
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "Error";
        };
    }
}