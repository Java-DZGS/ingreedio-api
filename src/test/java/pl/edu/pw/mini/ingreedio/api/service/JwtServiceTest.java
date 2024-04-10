package pl.edu.pw.mini.ingreedio.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    JwtService jwtService;

    @Mock
    UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret",
            "aW5ncmVlZGlvMTIzNDU2Nzg5MDEyMzQ1Njc4OTBpbmdyZWVkaW8=");
        ReflectionTestUtils.setField(jwtService, "accessTokenLifetime",
            10000);
    }

    @Test
    void givenUsername_whenGeneratingToken_thenTokenShouldBeGenerated() {
        // Given
        String username = "testUser";

        // When
        String token = jwtService.generateToken(username);

        // Then
        assertNotNull(token);
    }

    @Test
    void givenValidToken_whenExtractingUsername_thenUsernameShouldBeReturned() {
        // Given
        String token = jwtService.generateToken("testUser");

        // When
        String username = jwtService.extractUsername(token);

        // Then
        assertEquals("testUser", username);
    }

    @Test
    void givenValidToken_whenExtractingExpiration_thenExpirationDateShouldBeReturned() {
        // Given
        String token = jwtService.generateToken("testUser");

        // When
        Date expiration = jwtService.extractExpiration(token);

        // Then
        assertNotNull(expiration);
    }

    @Test
    void givenValidToken_whenExtractingClaim_thenClaimShouldBeReturned() {
        // Given
        String token = jwtService.generateToken("testUser");

        // When
        String subject = jwtService.extractClaim(token, Claims::getSubject);

        // Then
        assertEquals("testUser", subject);
    }

    @Test
    void givenValidTokenAndMatchingUserDetails_whenValidatingToken_thenTokenShouldBeValid() {
        // Given
        String token = jwtService.generateToken("testUser");
        when(userDetails.getUsername()).thenReturn("testUser");

        // When
        boolean valid = jwtService.isTokenValid(token, userDetails);

        // Then
        assertTrue(valid);
    }
}
