package pl.edu.pw.mini.ingreedio.api.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import pl.edu.pw.mini.ingreedio.api.dto.AuthRequestDto;
import pl.edu.pw.mini.ingreedio.api.dto.JwtResponseDto;
import pl.edu.pw.mini.ingreedio.api.dto.RefreshTokenRequestDto;
import pl.edu.pw.mini.ingreedio.api.dto.RegisterRequestDto;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    @Order(1)
    public void givenValidRegistrationRequest_whenRegister_thenTokenGenerated() {
        // Given
        RegisterRequestDto request = new RegisterRequestDto("us", "Us", "us@as.pl", "pass");

        // When
        JwtResponseDto response = authService.register(request);

        // Then
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
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
    @Order(4)
    void givenInvalidUsernameLoginRequest_whenLogin_thenBadCredentialsExceptionThrown() {
        // Given
        AuthRequestDto request = new AuthRequestDto("invalid", "password");

        // When / Then
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    @Order(5)
    void givenInvalidPasswordLoginRequest_whenLogin_thenBadCredentialsExceptionThrown() {
        // Given
        AuthRequestDto request = new AuthRequestDto("us", "password");

        // When / Then
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    @Order(6)
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
    @Order(7)
    void givenInvalidRefreshTokenRequest_whenRefresh_thenExceptionThrown() {
        // Given
        RefreshTokenRequestDto refreshTokenRequest = new RefreshTokenRequestDto("token");

        // When / Then
        assertThrows(RuntimeException.class, () -> authService.refresh(refreshTokenRequest));
    }
}
