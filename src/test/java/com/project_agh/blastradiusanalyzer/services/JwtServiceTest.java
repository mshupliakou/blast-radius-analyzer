package com.project_agh.blastradiusanalyzer.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JwtService}.
 * <p>
 * Validates JWT generation, username extraction, token uniqueness, and
 * rejection of invalid, null, or empty tokens.
 * </p>
 */
@SpringBootTest
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    /** A generated token should be valid according to the service. */
    @Test
    void generatesValidToken() {
        String token = jwtService.generateToken("alice");
        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
    }

    /** The username should be extractable from a generated token. */
    @Test
    void extractsUsername() {
        String token = jwtService.generateToken("bob");
        assertEquals("bob", jwtService.extractUsername(token));
    }

    /** Each call to generateToken should produce a unique token (due to random JTI). */
    @Test
    void eachTokenIsUnique() {
        String t1 = jwtService.generateToken("carol");
        String t2 = jwtService.generateToken("carol");
        assertNotEquals(t1, t2, "Two tokens for the same user must be different");
    }

    /** A malformed token should be rejected. */
    @Test
    void rejectsInvalidToken() {
        assertFalse(jwtService.isTokenValid("garbage.token.here"));
    }

    /** A null token should be rejected. */
    @Test
    void rejectsNullToken() {
        assertFalse(jwtService.isTokenValid(null));
    }

    /** An empty token should be rejected. */
    @Test
    void rejectsEmptyToken() {
        assertFalse(jwtService.isTokenValid(""));
    }

    /** Different users should receive different tokens. */
    @Test
    void differentUsersGetDifferentTokens() {
        String t1 = jwtService.generateToken("alice");
        String t2 = jwtService.generateToken("bob");
        assertNotEquals(t1, t2);
    }
}
