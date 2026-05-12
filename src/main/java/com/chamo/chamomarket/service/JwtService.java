package com.chamo.chamomarket.service;

import com.chamo.chamomarket.dto.auth.TokenDataDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // aqui generamos token con id, role, username jeje
    public String generateToken(Long employeeId, String role, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("employeeId", employeeId);
        claims.put("role", role);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigninKey())
                .compact();
    }

    // here we validate the token
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(getSigninKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // extract data
    public TokenDataDTO extractTokenData(String token) {

        String username = extractUserName(token);
        Long employeeId = extractEmployeeId(token);
        String role = extractRole(token);

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

    public <T> T extractClaims(String token, Function<Claims, T> resolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return resolver.apply(claims);
    }

    // METODOS ESPECIFICOS PARA EXTRAER DATOS

    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Long extractEmployeeId(String token) {
        return extractClaims(token, claims -> claims.get("employeeId", Long.class));
    }

    public String extractRole(String token) {
        return extractClaims(token, claims -> claims.get("role", String.class));
    }
}