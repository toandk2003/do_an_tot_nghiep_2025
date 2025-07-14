package org.yenln8.ChatApp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMs);
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("email", email);
        claims.put("roles", roles);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(this.getKey())
                .compact();
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
        try {
            return Jwts.parserBuilder().setSigningKey(this.getKey()).build().parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }
}