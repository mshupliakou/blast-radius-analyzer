package com.project_agh.blastradiusanalyzer.services;

import com.project_agh.blastradiusanalyzer.config.TestNeo4jConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestNeo4jConfig.class)
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void generatesValidToken() {
        String token = jwtService.generateToken("alice");
        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void extractsUsername() {
        String token = jwtService.generateToken("bob");
        assertEquals("bob", jwtService.extractUsername(token));
    }

    @Test
    void eachTokenIsUnique() {
        String t1 = jwtService.generateToken("carol");
        String t2 = jwtService.generateToken("carol");
        assertNotEquals(t1, t2, "Two tokens for the same user must be different");
    }

    @Test
    void rejectsInvalidToken() {
        assertFalse(jwtService.isTokenValid("garbage.token.here"));
    }

    @Test
    void rejectsNullToken() {
        assertFalse(jwtService.isTokenValid(null));
    }

    @Test
    void rejectsEmptyToken() {
        assertFalse(jwtService.isTokenValid(""));
    }

    @Test
    void differentUsersGetDifferentTokens() {
        String t1 = jwtService.generateToken("alice");
        String t2 = jwtService.generateToken("bob");
        assertNotEquals(t1, t2);
    }
}
