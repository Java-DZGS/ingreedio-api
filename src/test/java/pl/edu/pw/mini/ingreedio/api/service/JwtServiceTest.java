package pl.edu.pw.mini.ingreedio.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pl.edu.pw.mini.ingreedio.api.security.JwtUserClaims;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    JwtService jwtService;

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
        JwtUserClaims jwtUserClaimsDto = JwtUserClaims.builder()
            .username("testUser")
            .build();

        // When
        String token = jwtService.generateToken(jwtUserClaimsDto);

        // Then
        assertNotNull(token);
    }

    @Test
    void givenUsernameRolesPermissions_whenGeneratingToken_thenTokenShouldBeGenerated() {
        // Given
        JwtUserClaims jwtUserClaimsDto = JwtUserClaims.builder()
            .username("testUser")
            .roles(Set.of("MODERATOR", "USER"))
            .permissions(Set.of("CAN_REMOVE_OPINION"))
            .build();

        // When
        String token = jwtService.generateToken(jwtUserClaimsDto);

        // Then
        assertNotNull(token);
    }

    @Test
    void givenValidToken_whenExtractingUsername_thenUsernameShouldBeReturned() {
        // Given
        JwtUserClaims jwtUserClaimsDto = JwtUserClaims.builder()
            .username("testUser")
            .build();

        String token = jwtService.generateToken(jwtUserClaimsDto);

        // When
        String username = jwtService.extractUsername(token);

        // Then
        assertEquals("testUser", username);
    }

    @Test
    void givenValidToken_whenExtractingExpiration_thenExpirationDateShouldBeReturned() {
        // Given
        JwtUserClaims jwtUserClaimsDto = JwtUserClaims.builder()
            .username("testUser")
            .build();

        String token = jwtService.generateToken(jwtUserClaimsDto);

        // When
        Date expiration = jwtService.extractExpiration(token);

        // Then
        assertNotNull(expiration);
    }

    @Test
    void givenValidToken_whenExtractingClaim_thenClaimShouldBeReturned() {
        // Given
        JwtUserClaims jwtUserClaimsDto = JwtUserClaims.builder()
            .username("testUser")
            .build();

        String token = jwtService.generateToken(jwtUserClaimsDto);

        // When
        String subject = jwtService.extractClaim(token, Claims::getSubject);

        // Then
        assertEquals("testUser", subject);
    }

    @Test
    void givenValidTokenMatchingUsernameAndRolesAndPermissions_whenValidatingToken_thenTokenShouldBeValid() {
        // Given
        JwtUserClaims jwtUserClaimsDto = JwtUserClaims.builder()
            .username("testUser")
            .roles(Set.of("MODERATOR", "USER"))
            .permissions(Set.of("CAN_REMOVE_OPINION"))
            .build();

        String token = jwtService.generateToken(jwtUserClaimsDto);

        // When
        boolean valid = jwtService.isTokenValid(token, jwtUserClaimsDto);

        // Then
        assertTrue(valid);
    }

    @Test
    void givenValidToken_whenExtractingRolesClaim_thenClaimShouldBeReturned() {
        // Given
        JwtUserClaims jwtUserClaimsDto = JwtUserClaims.builder()
            .username("testUser")
            .roles(Set.of("MODERATOR", "USER"))
            .permissions(Set.of("CAN_REMOVE_OPINION"))
            .build();

        String token = jwtService.generateToken(jwtUserClaimsDto);

        // When
        Set<String> roles = jwtService.extractRoles(token);

        // Then
        assertEquals(jwtUserClaimsDto.roles(), roles);
    }

    @Test
    void givenValidToken_whenExtractingPermissionClaim_thenClaimShouldBeReturned() {
        // Given
        JwtUserClaims jwtUserClaimsDto = JwtUserClaims.builder()
            .username("testUser")
            .roles(Set.of("MODERATOR", "USER"))
            .permissions(Set.of("CAN_REMOVE_OPINION"))
            .build();

        String token = jwtService.generateToken(jwtUserClaimsDto);

        // When
        Set<String> permissions = jwtService.extractPermissions(token);

        // Then
        assertEquals(jwtUserClaimsDto.permissions(), permissions);
    }
}
