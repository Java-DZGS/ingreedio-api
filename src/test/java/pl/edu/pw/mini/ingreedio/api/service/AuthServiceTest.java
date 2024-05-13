package pl.edu.pw.mini.ingreedio.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import pl.edu.pw.mini.ingreedio.api.IntegrationTest;
import pl.edu.pw.mini.ingreedio.api.auth.dto.AuthRequestDto;
import pl.edu.pw.mini.ingreedio.api.auth.dto.JwtResponseDto;
import pl.edu.pw.mini.ingreedio.api.auth.dto.RefreshTokenRequestDto;
import pl.edu.pw.mini.ingreedio.api.auth.dto.RegisterRequestDto;
import pl.edu.pw.mini.ingreedio.api.auth.model.Role;
import pl.edu.pw.mini.ingreedio.api.auth.service.AuthService;
import pl.edu.pw.mini.ingreedio.api.auth.service.RoleService;
import pl.edu.pw.mini.ingreedio.api.user.model.User;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthServiceTest extends IntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private RoleService roleService;

    @Test
    @Order(1)
    public void givenValidRegistrationRequest_whenRegister_thenTokenGenerated() {
        // Given
        RegisterRequestDto request = new RegisterRequestDto("us", "Us", "us@as.pl", "pass");

        // When
        User response = authService.register(request);

        // Then
        assertEquals("Us", response.getDisplayName());
        assertEquals("us@as.pl", response.getEmail());
    }

    @Test
    @Order(2)
    public void givenDuplicateUsernameRegisterRequest_whenRegister_thenExceptionThrown() {
        // Given
        RegisterRequestDto request = new RegisterRequestDto("us", "Us", "us@as.pl", "pass");

        // When / Then
        assertThrows(DataIntegrityViolationException.class, () -> authService.register(request));
    }

    @Test
    @Order(3)
    public void givenValidRegistrationRequest_whenRegister_thenNewUserHasDefaultRoles() {
        // Given
        Set<String> newUserRoles = authService.getAuthInfoByUsername("us").getRoles()
            .stream().map(Role::getName).collect(Collectors.toSet());
        Set<String> defaultRoles = roleService.getDefaultUserRoles()
            .stream().map(Role::getName).collect(Collectors.toSet());

        // When
        boolean valid = newUserRoles.equals(defaultRoles);

        // Then
        assertTrue(valid);
    }

    @Test
    @Order(4)
    void givenValidLoginRequest_whenLogin_thenTokensGenerated() {
        // Given
        AuthRequestDto request = new AuthRequestDto("us", "pass");

        // When
        JwtResponseDto response = authService.login(request);

        // Then
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
    }

    @Test
    @Order(5)
    void givenInvalidUsernameLoginRequest_whenLogin_thenBadCredentialsExceptionThrown() {
        // Given
        AuthRequestDto request = new AuthRequestDto("invalid", "password");

        // When / Then
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    @Order(6)
    void givenInvalidPasswordLoginRequest_whenLogin_thenBadCredentialsExceptionThrown() {
        // Given
        AuthRequestDto request = new AuthRequestDto("us", "password");

        // When / Then
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    @Order(7)
    void givenValidRefreshTokenRequest_whenRefresh_thenNewTokenGenerated() {
        // Given
        AuthRequestDto loginRequest = new AuthRequestDto("us", "pass");
        JwtResponseDto loginResponse = authService.login(loginRequest);
        RefreshTokenRequestDto refreshTokenRequest =
            new RefreshTokenRequestDto(loginResponse.refreshToken());

        // When
        JwtResponseDto refreshTokenResponse = authService.refresh(refreshTokenRequest);

        // Then
        assertNotNull(refreshTokenResponse.accessToken());
        assertNotNull(refreshTokenResponse.refreshToken());
    }

    @Test
    @Order(8)
    void givenInvalidRefreshTokenRequest_whenRefresh_thenExceptionThrown() {
        // Given
        RefreshTokenRequestDto refreshTokenRequest = new RefreshTokenRequestDto("token");

        // When / Then
        assertThrows(RuntimeException.class, () -> authService.refresh(refreshTokenRequest));
    }
}
