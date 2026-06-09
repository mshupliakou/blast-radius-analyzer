package com.project_agh.blastradiusanalyzer.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * Service for generating, parsing, and validating JWT tokens.
 * <p>
 * Uses the HMAC-SHA algorithm configured with a secret key obtained from
 * the {@code jwt.secret} application property. Tokens expire after 24
 * hours and include a unique identifier to prevent replay.
 * </p>
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private static final long EXPIRATION_TIME = 86400000;

    /**
     * Builds the HMAC-SHA signing key from the configured secret string.
     *
     * @return a {@link SecretKey} suitable for JWT signing and verification
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a signed JWT for the given username.
     *
     * @param username the subject to embed in the token
     * @return a compact, signed JWT string
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .id(UUID.randomUUID().toString())
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the username (subject) from a valid JWT.
     *
     * @param token the JWT string to parse
     * @return the subject claim from the token
     */
    public String extractUsername(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * Checks whether the given token is a valid, non-expired JWT.
     *
     * @param token the JWT string to validate
     * @return {@code true} if the token is valid, {@code false} otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            extractUsername(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}