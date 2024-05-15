package pl.edu.pw.mini.ingreedio.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

public class AuthServiceTest extends IntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private RoleService roleService;

    @Test
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
    public void givenDuplicateUsernameRegisterRequest_whenRegister_thenExceptionThrown() {
        // Given
        RegisterRequestDto request = new RegisterRequestDto("user", "User", "us@as.pl", "pass");

        // When / Then
        assertThrows(DataIntegrityViolationException.class, () -> authService.register(request));
    }

    @Test
    public void givenValidRegistrationRequest_whenRegister_thenNewUserHasDefaultRoles() {
        // Given
        RegisterRequestDto request = new RegisterRequestDto("us", "Us", "us@as.pl", "pass");
        authService.register(request);

        Set<String> defaultRoles = roleService.getDefaultUserRoles()
            .stream().map(Role::getName).collect(Collectors.toSet());
        Set<String> newUserRoles = authService.getAuthInfoByUsername("us").getRoles()
            .stream().map(Role::getName).collect(Collectors.toSet());

        // When
        boolean valid = newUserRoles.equals(defaultRoles);

        // Then
        assertTrue(valid);
    }

    @Test
    void givenValidLoginRequest_whenLogin_thenTokensGenerated() {
        // Given
        AuthRequestDto request = new AuthRequestDto("user", "user");

        // When
        JwtResponseDto response = authService.login(request);

        // Then
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
    }

    @Test
    void givenInvalidUsernameLoginRequest_whenLogin_thenBadCredentialsExceptionThrown() {
        // Given
        AuthRequestDto request = new AuthRequestDto("invalid", "password");

        // When / Then
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void givenInvalidPasswordLoginRequest_whenLogin_thenBadCredentialsExceptionThrown() {
        // Given
        AuthRequestDto request = new AuthRequestDto("us", "password");

        // When / Then
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void givenValidRefreshTokenRequest_whenRefresh_thenNewTokenGenerated() {
        // Given
        AuthRequestDto loginRequest = new AuthRequestDto("user", "user");
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
    void givenInvalidRefreshTokenRequest_whenRefresh_thenExceptionThrown() {
        // Given
        RefreshTokenRequestDto refreshTokenRequest = new RefreshTokenRequestDto("token");

        // When / Then
        assertThrows(RuntimeException.class, () -> authService.refresh(refreshTokenRequest));
    }
}
