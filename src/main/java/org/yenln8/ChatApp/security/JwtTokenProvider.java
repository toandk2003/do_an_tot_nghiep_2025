package org.yenln8.ChatApp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value(value = "${app.security.jwt.key}")
    private String key;

    @Value(value = "${app.security.jwt.expire.time}")
    private Long validityInMs; // 10 hour

    private Key getKey() {
        return Keys.hmacShaKeyFor(key.getBytes());
    }

    public String createToken(Long id,String email, List<String> roles) {
        log.info("using key to gen token: " + this.key);
        log.info("using validityInMs to gen token: " + this.validityInMs);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMs);
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("email", email);
        claims.put("roles", roles);
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(this.getKey())
                .compact();
        log.info("token is generate: " + token);

        return token;
    }

    public Long getId(Claims claims) {
        return claims.get("id", Long.class);
    }

    public String getEmail(Claims claims) {
        return claims.get("email", String.class);
    }

    public List<String> getRoles(Claims claims) {
        return claims.get("roles", List.class);
    }

    public boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }


    public Claims decodeToken(String token) {
        log.info("using key to decode token: " + this.key);
        log.info("using validityInMs to decode token: " + this.validityInMs);
        if(token == null) return null;

        try {
            return Jwts.parserBuilder().setSigningKey(this.getKey()).build().parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }
}