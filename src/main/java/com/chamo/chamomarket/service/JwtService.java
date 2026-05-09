package com.chamo.chamomarket.service;

import com.chamo.chamomarket.dto.auth.TokenDataDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // aqui generamos token con id, role, username jeje
    public String generateToken(Long employeeId, String role, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("employeeId", employeeId);
        claims.put("role", role);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // here we validate the token
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // extract data
    public TokenDataDTO extractTokenData(String token) {
        Claims claims = extractAllClaims(token);

        String username = claims.getSubject();
        Long employeeId = claims.get("employeeId", Long.class);
        String role = claims.get("role", String.class);

        return new TokenDataDTO(username, employeeId, role);
    }

    // refresh :p
    public String refreshToken(String token) {
        if (!isTokenValid(token)) {
            throw new RuntimeException("Token invalido o vencido");
        }

        TokenDataDTO data = extractTokenData(token);
        return generateToken(data.getEmployeeId(), data.getRole(), data.getUsername());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}