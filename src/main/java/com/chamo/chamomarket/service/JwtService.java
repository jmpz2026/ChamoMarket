package com.chamo.chamomarket.service;

import com.chamo.chamomarket.dto.auth.TokenDataDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
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

    // aqui generamos token con id, rolId, username
    public String generateToken(Long employeeId, Long rolId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("employeeId", employeeId);
        claims.put("rolId", rolId);

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

        return generateToken(
                extractEmployeeId(token),
                extractRolId(token),
                extractUserName(token)
        );
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

    public Long extractRolId(String token) {
        return extractClaims(token, claims -> claims.get("rolId", Long.class));
    }

    public String extractRole(String token) {
        return extractClaims(token, claims -> claims.get("role", String.class));
    }
}