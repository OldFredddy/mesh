package com.chsbk.mesh.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtService {

    private final SecretKey secretKey;
    private final Duration expiration;

    public JwtService(@Value("${mesh.security.jwt.secret}") String secret,
                      @Value("${mesh.security.jwt.expiration-seconds:86400}") long expirationSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = Duration.ofSeconds(expirationSeconds);
    }

    public String generateToken(long userId) {
        Instant issuedAt = Instant.now();
        return Jwts.builder()
                .setSubject(Long.toString(userId))
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(issuedAt.plus(expiration)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }
}
